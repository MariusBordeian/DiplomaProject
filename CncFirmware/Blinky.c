/*************************************************************************************************
*	Authors : Ostafciuc Andrei, Bordeian Marius, Keil uVision and good smart guys from the internet*
	Any notes shell go here ___ :
	
*
**************************************************************************************************/

#include "stm32f4xx.h"
#include "stm32f4xx_exti.h"
#include "stm32f4xx_syscfg.h"
#include "tm_stm32f4_disco.h"
#include "tm_stm32f4_usart.h"
#include "defines.h"

#include <math.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#define OFF 0
#define ON 1
#define POSITIVE 1
#define NEGATIVE -1
#define X 'x'
#define Y 'y'
#define Z 'z'

// Global declarations START
uint32_t multiplier;	// ???

GPIO_InitTypeDef	GPIO_InitStructureC;		// ???
GPIO_InitTypeDef	GPIO_InitStructureE;		// ???

// the delay between motor's steps in the line(*function*)
uint32_t delay = 460; /* stable minimum aka maximum speed
							200 -> 460
							400 ->
							800 ->
							1600-> 60
						*/
uint16_t c;
double dest[3] = {0};	
int nrOfCoordinatesReceived = 0;	
char serialCoordinates[256] = {0};
char buffer[256] = {0};

float px = 0, 
			py = 0, 
			pz = 0; // global positions on every specific axe

// Global declarations END

void TM_Delay_Init(void) {
	RCC_ClocksTypeDef RCC_Clocks;

	/* Get system clocks */
	RCC_GetClocksFreq(&RCC_Clocks);

	/* While loop takes 4 cycles */
	/* For 1 us delay, we need to divide with 4M */
	multiplier = RCC_Clocks.HCLK_Frequency / 4000000;
}
void TM_DelayMicros(uint32_t micros) {
	/* Multiply micros with multipler */
	/* Substract 10 */
	micros = micros * multiplier - 10;
	/* 4 cycles for one loop */
	while (micros--);
}
void TM_DelayMillis(uint32_t millis) {
	/* Multiply millis with multipler */
	/* Substract 10 */
	millis = 1000 * millis * multiplier - 10;
	/* 4 cycles for one loop */
	while (millis--);
}



void spindlePowerToggle(int state) {
	(state == OFF) ? GPIO_ResetBits(GPIOC, GPIO_Pin_9) : GPIO_SetBits(GPIOC, GPIO_Pin_9);
}
int getNrOfDelim(char src[], char delim) {
	int m = 0, delimCount = 0;
	for (m = 0; src[m]; m++) {
		if (src[m] == delim) {
			delimCount++;
		}
	}
	return delimCount;
}
int getNumbers(char src[], double dest[]) {
	char *splitedArray;
	int capacity = 4;		// sizeof(dest)/sizeof(double) || dest.Count
	int nrOfDelims = 0;
	int srcLen = 0;
	int i = 0;
		
	dest[0] = 0;
	dest[1] = 0;
	dest[2] = 0;
	
	srcLen = strlen(src);
	
	if (srcLen > 0) {
		for (i = 0; i < srcLen; i++) {
			if (!(
						(src[i] >= '0' && src[i] <= '9') || 
							src[i] == '.' ||
							src[i] == ',' ||
							src[i] == '#' || 
							src[i] == '-' ||
							src[i] == '@' ||
							src[i] == '\n'
						)
				 ) 
			{
				return 0;
			}
		}
		
		if (srcLen == 3 && src[0] == '@')
		{
			if (src[1] == '0')
				spindlePowerToggle(OFF);
			else if (src[1] == '1')
				spindlePowerToggle(ON);
			
			return 0;
		}
		
		nrOfDelims = getNrOfDelim(src, '#');
		splitedArray = strtok(src, "#");
		delay = atoi(splitedArray);
		splitedArray = strtok(NULL, "#");
			
		if (nrOfDelims == 1) {
			dest[0] = atof(splitedArray);
		}
		else {
			i = 0;
			while (i<capacity && splitedArray != NULL) {
				dest[i++] = atof(splitedArray);
				splitedArray = strtok(NULL, "#");
			}
		}
		return nrOfDelims;
	}
	else
	{
		return 0;
	}
}
int getNumberOfSteps(float milim) {
	int stepScale = 1600;
	return stepScale * milim / 3;		//stepScale - nr. of stepts for 3mm distance
}
double getMM(double steps) {
	return 3 * steps / 1600;
}


void lineZ(long newZ) {
	long nrOfSteps=newZ-pz; // dz
	uint32_t delay_backup = delay;
	uint32_t i = 0;

	if (nrOfSteps > 0) {
		GPIO_SetBits(GPIOE, GPIO_Pin_2);	 // dir CCW -
	} else {
		GPIO_ResetBits(GPIOE, GPIO_Pin_2); // dir CW  +
	}
	nrOfSteps = fabs(nrOfSteps);

	if (nrOfSteps >= 800) {
		for (i = 0; i < nrOfSteps; i++) {
			if (i <= 400) {
				delay--;
			} else if (i >= nrOfSteps - 400) {
				delay++;
			}			
			GPIO_SetBits(GPIOE, GPIO_Pin_3);
			TM_DelayMicros(delay);
			GPIO_ResetBits(GPIOE, GPIO_Pin_3);
			TM_DelayMicros(delay);
		}
	} else {
		for (i = 0; i < nrOfSteps; i++) {
			if (i <= nrOfSteps / 2) {
				delay--;
			} else {
				delay++;
			}
			GPIO_SetBits(GPIOE, GPIO_Pin_3);
			TM_DelayMicros(delay);
			GPIO_ResetBits(GPIOE, GPIO_Pin_3);
			TM_DelayMicros(delay);
		}
	}
	pz = newZ;
	delay = delay_backup;
}


void line(float newx, float newy) {
	long dx = newx - px;
	long dy = newy - py;
	long i;
	long over = 0;
	int dirx = dx > 0 ? POSITIVE : NEGATIVE;
	int diry = dy > 0 ? POSITIVE : NEGATIVE;
	dx = fabs(dx);
	dy = fabs(dy);

	(dirx == POSITIVE) ? GPIO_SetBits(GPIOE, GPIO_Pin_4) : GPIO_ResetBits(GPIOE, GPIO_Pin_4);
	(diry == POSITIVE) ? GPIO_SetBits(GPIOC, GPIO_Pin_6) : GPIO_ResetBits(GPIOC, GPIO_Pin_6);

	if (dx > dy) {
		for (i = 0; i < dx; ++i) {
			GPIO_SetBits(GPIOE, GPIO_Pin_5);
			over += dy;
			if (over >= dx) {
				over -= dx;
				GPIO_SetBits(GPIOC, GPIO_Pin_7);
			}
			TM_DelayMicros(delay);
			GPIO_ResetBits(GPIOE, GPIO_Pin_5);
			GPIO_ResetBits(GPIOC, GPIO_Pin_7);
		}
	} else {
		for (i = 0; i < dy; ++i) {
			GPIO_SetBits(GPIOC, GPIO_Pin_7);
			over += dx;
			if (over >= dy) {
				over -= dy;
				GPIO_SetBits(GPIOE, GPIO_Pin_5);
			}
			TM_DelayMicros(delay);
			GPIO_ResetBits(GPIOC, GPIO_Pin_7);
			GPIO_ResetBits(GPIOE, GPIO_Pin_5);
		}
	}

	px = newx;
	py = newy;
}


void handShake() {
		sprintf(serialCoordinates, "%f#%f#%f\n", getMM(px), getMM(py), getMM(pz));	// as above about the '#' and '\n'
		while (1) 
		{
			c = TM_USART_Gets(USART1, buffer, 256);
			if (c) 
			{
				break;
			}
			else
			{
				TM_USART_Puts(USART1, serialCoordinates);
			}
			
			TM_DelayMillis(1000);
		}
}



int main (void) {
	uint32_t i = 0;
	GPIO_ResetBits(GPIOC, GPIO_Pin_8);

	RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOE, ENABLE);
	RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOC, ENABLE);

	GPIO_InitStructureE.GPIO_Pin = GPIO_Pin_2 | GPIO_Pin_3 | GPIO_Pin_4 | GPIO_Pin_5;
	GPIO_InitStructureE.GPIO_Mode = GPIO_Mode_OUT;
	GPIO_InitStructureE.GPIO_OType = GPIO_OType_PP;
	GPIO_InitStructureE.GPIO_Speed = GPIO_Speed_100MHz;
	GPIO_InitStructureE.GPIO_PuPd = GPIO_PuPd_NOPULL;
	GPIO_Init(GPIOE, &GPIO_InitStructureE);

	GPIO_InitStructureC.GPIO_Pin = GPIO_Pin_6 | GPIO_Pin_7 | GPIO_Pin_8 | GPIO_Pin_9;
	GPIO_InitStructureC.GPIO_Mode = GPIO_Mode_OUT;
	GPIO_InitStructureC.GPIO_OType = GPIO_OType_PP;
	GPIO_InitStructureC.GPIO_Speed = GPIO_Speed_100MHz;
	GPIO_InitStructureC.GPIO_PuPd = GPIO_PuPd_NOPULL;
	GPIO_Init(GPIOC, &GPIO_InitStructureC);
	
	/* System init */
	SystemInit();

	/* Initialize delay */
	TM_Delay_Init();

	/* Initialize USART1 at 115200 baud, TX: PB6, RX: PB7 */
	TM_USART_Init(USART1, TM_USART_PinsPack_2, 115200);

	handShake();
	//TM_USART_Puts(USART1, serialCoordinates);
	while(1){
		for (i = 0; i < 256; ++i) {
			buffer[i] = 0;
			serialCoordinates[i] = 0;
		}
		c = TM_USART_Gets(USART1, buffer, 256);
		if(c){
			nrOfCoordinatesReceived = getNumbers(buffer, dest);
			if (nrOfCoordinatesReceived == 2)
			{
				line(getNumberOfSteps(dest[0]), getNumberOfSteps(dest[1]));
			}
			else if (nrOfCoordinatesReceived == 1)
			{
				lineZ(getNumberOfSteps(dest[0]));
			}
			else if (nrOfCoordinatesReceived == 3)
			{
				px = getNumberOfSteps(dest[0]);
				py = getNumberOfSteps(dest[1]);
				pz = getNumberOfSteps(dest[2]);
			}
			sprintf(serialCoordinates, "%f#%f#%f\n", getMM(px), getMM(py), getMM(pz));	// as above about the '#' and '\n'
			TM_USART_Puts(USART1, serialCoordinates);
		}
	}
}	
