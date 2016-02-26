/*************************************************************************************************
*	Authors : Ostafciuc Andrei, Bordeian Marius, Keil uVision and good smart guys from the internet*
	Any notes shell go here ___ :
	
*
**************************************************************************************************/

#include <stdio.h>
#include "stm32f4xx.h"
#include "stm32f4xx_exti.h"
#include "stm32f4xx_syscfg.h"
#include "misc.h"
#include "LED.h"
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include "defines.h"
#include "tm_stm32f4_disco.h"
#include "tm_stm32f4_usart.h"

#define OFF 0
#define ON 1
#define POSITIVE 1
#define NEGATIVE -1
#define X 'x'
#define Y 'y'
#define Z 'z'

// Global declarations START
uint32_t multiplier;	// ???
uint32_t counter = 0; // ???
long double PI = 3.141592653589793238462;

uint32_t btns = 0;			// ???

GPIO_InitTypeDef	GPIO_InitStructureC;		// ???
GPIO_InitTypeDef	GPIO_InitStructureE;		// ???

int step = 0;	// amount to add to theta each time (degrees) ??? 	// drawCircle(*function*) related

int MM_PER_SEGMENT = 10; // static arc(*function*) related

uint32_t step_delay = 460;	// the delay between motor's steps in the line(*function*)

float px, py, pz; // global positions on every specific axe

volatile uint32_t msTicks;		// counts 1ms timeTicks		// SysTick_Handler(*function*) related
// Global declarations END

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
	int capacity = 3;		// sizeof(dest)/sizeof(double) || dest.Count
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
							src[i] == '\n'
						)
				 ) 
			{
				return 0;
			}
		}
		
		nrOfDelims = getNrOfDelim(src, '#');
		if (nrOfDelims == 0) {
			dest[0] = atof(src);
		}
		else {
			i = 0;
			splitedArray = strtok(src, "#");
			while (i<capacity && splitedArray != NULL) {
				dest[i++] = atof(splitedArray);
				splitedArray = strtok(NULL, "#");
			}
		}
		return nrOfDelims+1;
	}
	else
	{
		return 0;
	}
}

void spindle(int state) {
	(state == OFF) ? GPIO_ResetBits(GPIOC, GPIO_Pin_9) : GPIO_SetBits(GPIOC, GPIO_Pin_9);
}

void Configure_PB12(void) {
	/* Set variables used */
	GPIO_InitTypeDef GPIO_InitStruct;
	EXTI_InitTypeDef EXTI_InitStruct;
	NVIC_InitTypeDef NVIC_InitStruct;

	/* Enable clock for GPIOB */
	RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOB, ENABLE);
	/* Enable clock for SYSCFG */
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_SYSCFG, ENABLE);

	/* Set pin as input */
	GPIO_InitStruct.GPIO_Mode = GPIO_Mode_IN;
	GPIO_InitStruct.GPIO_OType = GPIO_OType_PP;
	GPIO_InitStruct.GPIO_Pin = GPIO_Pin_12;
	GPIO_InitStruct.GPIO_PuPd = GPIO_PuPd_DOWN;
	GPIO_InitStruct.GPIO_Speed = GPIO_Speed_100MHz;
	GPIO_Init(GPIOB, &GPIO_InitStruct);

	/* Tell system that you will use PB12 for EXTI_Line12 */
	SYSCFG_EXTILineConfig(EXTI_PortSourceGPIOB, EXTI_PinSource12);

	/* PB12 is connected to EXTI_Line12 */
	EXTI_InitStruct.EXTI_Line = EXTI_Line12;
	/* Enable interrupt */
	EXTI_InitStruct.EXTI_LineCmd = ENABLE;
	/* Interrupt mode */
	EXTI_InitStruct.EXTI_Mode = EXTI_Mode_Interrupt;
	/* Triggers on rising and falling edge */
	EXTI_InitStruct.EXTI_Trigger = EXTI_Trigger_Falling;
	/* Add to EXTI */
	EXTI_Init(&EXTI_InitStruct);

	/* Add IRQ vector to NVIC */
	/* PB12 is connected to EXTI_Line12, which has EXTI15_10_IRQn vector */
	NVIC_InitStruct.NVIC_IRQChannel = EXTI15_10_IRQn;
	/* Set priority */
	NVIC_InitStruct.NVIC_IRQChannelPreemptionPriority = 0x00;
	/* Set sub priority */
	NVIC_InitStruct.NVIC_IRQChannelSubPriority = 0x01;
	/* Enable interrupt */
	NVIC_InitStruct.NVIC_IRQChannelCmd = ENABLE;
	/* Add to NVIC */
	NVIC_Init(&NVIC_InitStruct);
}

/* Handle PB12 interrupt */
void EXTI15_10_IRQHandler(void) {
	/* Make sure that interrupt flag is set */
	if (EXTI_GetITStatus(EXTI_Line12) != RESET) {
		/* Do your stuff when PB12 is changed */

		if (counter != 0) {
			LED_Out (0x0F);
			while (1) {
				GPIO_ResetBits(GPIOE, GPIO_Pin_5);
				GPIO_ResetBits(GPIOC, GPIO_Pin_7);
				GPIO_ResetBits(GPIOE, GPIO_Pin_3);
			}
		}

		counter++;
		/* Clear interrupt flag */
		EXTI_ClearITPendingBit(EXTI_Line12);
	}
}

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

void SysTick_Handler(void) {	// SysTick_Handler
	msTicks++;
}

void Delay (uint32_t dlyTicks) {		// delays number of tick Systicks (happens every 1 ms)
	uint32_t curTicks;
	curTicks = msTicks;
	while ((msTicks - curTicks) < dlyTicks);
}

void BTN_Init(void) {	// initializes Button pins
	RCC->AHB1ENR	|= ((1UL <<	0) );							/* Enable GPIOA clock				 */

	GPIOA->MODER		&= ~((3UL << 2 * 0)	);				 /* PA.0 is input							*/
	GPIOA->OSPEEDR	&= ~((3UL << 2 * 0)	);				 /* PA.0 is 50MHz Fast Speed	 */
	GPIOA->OSPEEDR	|=	((2UL << 2 * 0)	);
	GPIOA->PUPDR		&= ~((3UL << 2 * 0)	);				 /* PA.0 is no Pull up				 */
}

uint32_t BTN_Get(void) {	// read Button pins
	return (GPIOA->IDR & (1UL << 0));
}


int getNumberOfSteps(float milim) {
	int stepScale = 1600;
	return stepScale * milim / 3;		//stepScale - nr. of stepts for 3mm distance
}


void runDiag(uint32_t milimeters, uint32_t dir, char axa1, char axa2) {
	uint32_t nrOfSteps = getNumberOfSteps(milimeters) / 1.41421356237;
	uint32_t i = 0;
	uint32_t delay = 460;
	if (dir == POSITIVE) {
		switch (axa1) {
		case X : GPIO_SetBits(GPIOE, GPIO_Pin_4);	break; // dir CCW
		case Y : GPIO_SetBits(GPIOC, GPIO_Pin_6);	break; // dir CCW
		case Z : GPIO_ResetBits(GPIOE, GPIO_Pin_2);	break; // dir CW
		}
		switch (axa2) {
		case X : GPIO_SetBits(GPIOE, GPIO_Pin_4);	break; // dir CCW
		case Y : GPIO_SetBits(GPIOC, GPIO_Pin_6);	break; // dir CCW
		case Z : GPIO_ResetBits(GPIOE, GPIO_Pin_2);	break; // dir CW
		}
	} else {
		switch (axa1) {
		case X : GPIO_ResetBits(GPIOE, GPIO_Pin_4);	break; // dir CW
		case Y : GPIO_ResetBits(GPIOC, GPIO_Pin_6);	break; // dir CW
		case Z : GPIO_SetBits(GPIOE, GPIO_Pin_2);	break; // dir CCW
		}
		switch (axa2) {
		case X : GPIO_ResetBits(GPIOE, GPIO_Pin_4);	break; // dir CW
		case Y : GPIO_ResetBits(GPIOC, GPIO_Pin_6);	break; // dir CW
		case Z : GPIO_SetBits(GPIOE, GPIO_Pin_2);	break; // dir CCW
		}
	}

	if (nrOfSteps >= 800) {
		for (i = 0; i < nrOfSteps; i++) {
			if (i <= 400) {
				delay--;
			} else if (i >= nrOfSteps - 400) {
				delay++;
			}
			GPIO_SetBits(GPIOE, GPIO_Pin_5);
			GPIO_SetBits(GPIOC, GPIO_Pin_7);
			TM_DelayMicros(delay);
			GPIO_ResetBits(GPIOE, GPIO_Pin_5);
			GPIO_ResetBits(GPIOC, GPIO_Pin_7);
			TM_DelayMicros(delay);
		}

	} else {
		for (i = 0; i < nrOfSteps; i++) {
			if (i <= nrOfSteps / 2) {
				delay--;
			} else {
				delay++;
			}
			GPIO_SetBits(GPIOE, GPIO_Pin_5);
			GPIO_SetBits(GPIOC, GPIO_Pin_7);
			TM_DelayMicros(delay);
			GPIO_ResetBits(GPIOE, GPIO_Pin_5);
			GPIO_ResetBits(GPIOC, GPIO_Pin_7);
			TM_DelayMicros(delay);
		}
	}
}


void xStep(int dir) {
	(dir == NEGATIVE) ? GPIO_ResetBits(GPIOE, GPIO_Pin_4) : GPIO_SetBits(GPIOE, GPIO_Pin_4);
	GPIO_SetBits(GPIOE, GPIO_Pin_5);
	TM_DelayMicros(460);
	GPIO_ResetBits(GPIOE, GPIO_Pin_5);
	TM_DelayMicros(460);
}
void yStep(int dir) {
	(dir == NEGATIVE) ? GPIO_ResetBits(GPIOC, GPIO_Pin_6) : GPIO_SetBits(GPIOC, GPIO_Pin_6);
	GPIO_SetBits(GPIOC, GPIO_Pin_7);
	TM_DelayMicros(460);
	GPIO_ResetBits(GPIOC, GPIO_Pin_7);
	TM_DelayMicros(460);
}
void zStepP() {
	GPIO_ResetBits(GPIOE, GPIO_Pin_2);
	GPIO_SetBits(GPIOE, GPIO_Pin_3);
	TM_DelayMicros(460);
	GPIO_ResetBits(GPIOE, GPIO_Pin_3);
	TM_DelayMicros(460);
}
void zStepN() {
	GPIO_SetBits(GPIOE, GPIO_Pin_2);
	GPIO_SetBits(GPIOE, GPIO_Pin_3);
	TM_DelayMicros(460);
	GPIO_ResetBits(GPIOE, GPIO_Pin_3);
	TM_DelayMicros(460);
}

void run2(char axa, uint32_t nrOfSteps, int dir) {
	uint32_t i = 0;

	switch (axa) {
		case X : for (i = 0; i < nrOfSteps; i++) { xStep(dir); } break;
		case Y : for (i = 0; i < nrOfSteps; i++) { yStep(dir); } break;
		case Z : if (dir == POSITIVE ) { for (i = 0; i < nrOfSteps; i++) { zStepP(); } } else { for (i = 0; i < nrOfSteps; i++) { zStepN(); } } break;
	}
}

void run(char axa, int nrOfSteps) {
	int dir;
	uint32_t delay = 460;
	uint32_t i = 0;

	if (nrOfSteps > 0) {
		dir = POSITIVE;
	} else {
		dir = NEGATIVE;
	}
	nrOfSteps = fabs(nrOfSteps);

	if (dir == POSITIVE) {
		switch (axa) {
			case X : GPIO_SetBits(GPIOE, GPIO_Pin_4);	break; // dir CCW +
			case Y : GPIO_SetBits(GPIOC, GPIO_Pin_6);	break; // dir CCW +
			case Z : GPIO_ResetBits(GPIOE, GPIO_Pin_2);	break; // dir CW +
		}
	} else if (dir == NEGATIVE) {
		switch (axa) {
			case X : GPIO_ResetBits(GPIOE, GPIO_Pin_4);	break; // dir CW -
			case Y : GPIO_ResetBits(GPIOC, GPIO_Pin_6);	break; // dir CW -
			case Z : GPIO_SetBits(GPIOE, GPIO_Pin_2);	break; // dir CCW -
		}
	}

	if (nrOfSteps >= 800) {
		for (i = 0; i < nrOfSteps; i++) {
			if (i <= 400) {
				delay--;
			} else if (i >= nrOfSteps - 400) {
				delay++;
			}
			switch (axa) {
				case X :
					GPIO_SetBits(GPIOE, GPIO_Pin_5);
					TM_DelayMicros(delay);
					GPIO_ResetBits(GPIOE, GPIO_Pin_5);
					TM_DelayMicros(delay);
					break;
				case Y :
					GPIO_SetBits(GPIOC, GPIO_Pin_7);
					TM_DelayMicros(delay);
					GPIO_ResetBits(GPIOC, GPIO_Pin_7);
					TM_DelayMicros(delay);
					break;
				case Z :
					GPIO_SetBits(GPIOE, GPIO_Pin_3);
					TM_DelayMicros(delay);
					GPIO_ResetBits(GPIOE, GPIO_Pin_3);
					TM_DelayMicros(delay);
					break;
			}
		}
	} else {
		for (i = 0; i < nrOfSteps; i++) {
			if (i <= nrOfSteps / 2) {
				delay--;
			} else {
				delay++;
			}
			switch (axa) {
				case X :
					GPIO_SetBits(GPIOE, GPIO_Pin_5);
					TM_DelayMicros(delay);
					GPIO_ResetBits(GPIOE, GPIO_Pin_5);
					TM_DelayMicros(delay);
					break;
				case Y :
					GPIO_SetBits(GPIOC, GPIO_Pin_7);
					TM_DelayMicros(delay);
					GPIO_ResetBits(GPIOC, GPIO_Pin_7);
					TM_DelayMicros(delay);
					break;
				case Z :
					GPIO_SetBits(GPIOE, GPIO_Pin_3);
					TM_DelayMicros(delay);
					GPIO_ResetBits(GPIOE, GPIO_Pin_3);
					TM_DelayMicros(delay);
					break;
			}
		}

	}
	/* stable minimum aka maximum speed
		200 -> 460
		400 ->
		800 ->
		1600-> 60
	*/
	switch (axa) {
		case X :
			px = nrOfSteps;
			break;
		case Y :
			py = nrOfSteps;
			break;
		case Z :
			pz = nrOfSteps;
			break;
	}
}

void lineZ(long newZ) {
	long nrOfSteps=newZ-pz; // dz
	uint32_t delay = 460;
	uint32_t i = 0;

	if (nrOfSteps > 0) {
		GPIO_ResetBits(GPIOE, GPIO_Pin_2); // dir CW +
	} else {
		GPIO_SetBits(GPIOE, GPIO_Pin_2);	 // dir CCW -
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
			TM_DelayMicros(step_delay);
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
			TM_DelayMicros(step_delay);
			GPIO_ResetBits(GPIOC, GPIO_Pin_7);
			GPIO_ResetBits(GPIOE, GPIO_Pin_5);
		}
	}

	px = newx;
	py = newy;
}

/* not in use ATM
static float atan3(float dy, float dx) { // returns angle of dy/dx as a value from 0...2PI
	float a = atan2(dy, dx);
	if (a < 0) a = (PI * 2.0) + a;
	return a;
}
*/
/* not in use ATM
static void arc(float cx, float cy, float x, float y, float dir) {
	// This method assumes the limits have already been checked.
	// This method assumes the start and end radius match.
	// This method assumes arcs are not >180 degrees (PI radians)
	// cx/cy - center of circle
	// x/y - end position
	// dir - ARC_CW or ARC_CCW to control direction of arc
	
	// get radius
	int i;
	int segments;
	float len;
	float nx, ny, angle3, scale;
	float dx = px - cx;
	float dy = py - cy;
	float radius = sqrt(dx * dx + dy * dy);

	// find angle of arc (sweep)
	float angle1 = atan3(dy, dx);
	float angle2 = atan3(y - cy, x - cx);
	float theta = angle2 - angle1;

	if (dir > 0 && theta < 0) angle2 += 2 * PI;
	else if (dir < 0 && theta > 0) angle1 += 2 * PI;

	theta = angle2 - angle1;

	// get length of arc
	// float circ=PI*2.0*radius;
	// float len=theta*circ/(PI*2.0);
	// simplifies to
	len = fabs(theta) * radius;

	segments = ceil( len * MM_PER_SEGMENT );

	for (i = 0; i < segments; ++i) {
		// interpolate around the arc
		scale = ((float)i) / ((float)segments);

		angle3 = ( theta * scale ) + angle1;
		nx = cx + cos(angle3) * radius;
		ny = cy + sin(angle3) * radius;
		line(nx, ny);
	}
	line(x, y);
}
*/

double getMM(double steps) {
	return 3 * steps / 1600;
}

void drawCircle(int cx, int cy, int r) {		/* http://www.mathopenref.com/coordcirclealgorithm.html */
	float theta;															// angle that will be increased each loop
	float xx = 0;
	float yy = 0;
	theta = PI / 24;
	while ( step <= 48)
	{
		xx = cx + r * cos(step * theta);
		yy = cy + r * sin(step * theta);
		line(xx, yy);
		step++;
		//theta*=step;
	}
}

void drawCircle2(int x, int y, float r) {
	int theta = 5;
	float xx = 0;
	float yy = 0;
	int i = 0;

	while ( i < (360 / theta))
	{
		xx = x + r * cos(theta);
		yy = y + r * sin(theta);
		line(xx, yy);
		i++;
	}
}

int main (void) {
	double dest[3] = {0};
	uint16_t c;
	int nrOfCoordinatesReceived = 0;
	char buffer[256];
	char serialCoordinates[256];
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


	//SystemCoreClockUpdate();											/* Get Core Clock Frequency	 */
	//if (SysTick_Config(SystemCoreClock / 1000)) { /* SysTick 1 msec interrupts	*/
	//	while (1);																	/* Capture error							*/
	//}
	
	/* System init */
	SystemInit();
	LED_Init();
	BTN_Init();

	/* Configure PB12 as interrupt */
	//Configure_PB12();

	/* Initialize delay */
	TM_Delay_Init();

	/* Initialize USART1 at 115200 baud, TX: PB6, RX: PB7 */
	TM_USART_Init(USART1, TM_USART_PinsPack_2, 115200);

	while (1) {
			TM_USART_Puts(USART1, "@Ready\n");// '@' and '\n' are mandatory as special characters that server (aka RPi2) checks for to send coords for the line function from the GCode
			c = TM_USART_Gets(USART1, buffer, 256);
			if (c) {
				while (1) {
					if (c)
					{
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
						else if (nrOfCoordinatesReceived == 0) 
						{
							break;
						}

						sprintf(serialCoordinates, "%f#%f#%f\n", getMM(px), getMM(py), getMM(pz));	// as above about the '#' and '\n'

						TM_USART_Puts(USART1, serialCoordinates);
					}

					c = TM_USART_Gets(USART1, buffer, 256);
				}
			}
		}
	}


