//--------------------------------------------------------------
// File     : main.c
// Datum    : 13.04.2013
// Version  : 1.0
// Autor    : UB
// EMail    : mc-4u(@)t-online.de
// Web      : www.mikrocontroller-4u.de
// CPU      : STM32F4
// IDE      : CooCox CoIDE 1.7.0
// Module   : CMSIS_BOOT, M4_CMSIS_CORE
// Funktion : Demo der USB-MSC-HOST-Library
// Hinweis  : Diese zwei Files muessen auf 8MHz stehen
//              "cmsis_boot/stm32f4xx.h"
//              "cmsis_boot/system_stm32f4xx.c"
//--------------------------------------------------------------

#include "main.h"
#include "stm32_ub_led.h"
#include "stm32_ub_usb_msc_host.h"

int main(void)
{
  FIL myFile;   // Filehandler
  uint8_t write_ok=0;

  SystemInit(); // Quarz Einstellungen aktivieren

  // Init der LEDs
  UB_Led_Init();

  // Init vom USB-OTG-Port als MSC-HOST
  // (zum lesen/schreiben auf einen USB-Stick)
  UB_USB_MSC_HOST_Init();

  while(1)
  {
    // pollen vom USB-Status
    if(UB_USB_MSC_HOST_Do()==USB_MSC_DEV_CONNECTED) {
      // wenn USB-Stick erkannt wurde
      UB_Led_On(LED_GREEN);

      // wenn File noch nicht geschrieben wurde
      if(write_ok==0) {
       	write_ok=1;
       	UB_Led_On(LED_RED);
       	// Media mounten
       	if(UB_Fatfs_Mount(USB_0)==FATFS_OK) {
          // File zum schreiben im root neu anlegen
          if(UB_Fatfs_OpenFile(&myFile, "USB_File.txt", F_WR_CLEAR)==FATFS_OK) {
            // ein paar Textzeilen in das File schreiben
            UB_Fatfs_WriteString(&myFile,"Test der WriteString-Funktion");
            UB_Fatfs_WriteString(&myFile,"hier Zeile zwei");
            UB_Fatfs_WriteString(&myFile,"ENDE");
            // File schliessen
            UB_Fatfs_CloseFile(&myFile);
          }
          // Media unmounten
          UB_Fatfs_UnMount(USB_0);
        }
        UB_Led_Off(LED_RED);
      }
    }
    else {
      // wenn kein USB-Stick vorhanden
      UB_Led_Off(LED_GREEN);
    }
  }
}

