SET PATH=C:\Keil_v5\ARM\ARMCC\Bin;C:\Program Files (x86)\Common Files\Intel\Shared Files\cpp\bin\Intel64;C:\Python34;C:\Python34\Scripts;C:\ProgramData\Oracle\Java\javapath;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\adb;C:\Program Files\Microsoft SQL Server\110\Tools\Binn\;C:\Program Files (x86)\Windows Kits\8.1\Windows Performance Toolkit\;C:\Program Files (x86)\Common Files\Acronis\SnapAPI\;D:\non-Cloud\Program Files\MATLAB\R2012a\runtime\win64;D:\non-Cloud\Program Files\MATLAB\R2012a\bin;C:\Program Files\Java\jdk1.8.0_25\bin;C:\Program Files (x86)\QuickTime\QTSystem\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;D:\Cloud\Programs\Portable\SafeDriveEject;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\CooCox\CoIDE\gcc\bin;C:\Users\JumpMan\AppData\Roaming\Python\Scripts
SET CPU_TYPE=STM32F407VG
SET CPU_VENDOR=STMicroelectronics
SET UV2_TARGET=STM32F407 Flash
SET CPU_CLOCK=0x007A1200
"C:\Keil_v5\ARM\ARMCC\Bin\ArmAsm" --Via ".\flash\startup_stm32f40_41xxx._ia"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\system_stm32f4xx.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\led.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\blinky.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\stm32f4xx_flash.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\stm32f4xx_gpio.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\stm32f4xx_rcc.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\tm_stm32f4_disco.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\tm_stm32f4_gpio.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\stm32f4xx_exti.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\stm32f4xx_syscfg.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\misc.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\tm_stm32f4_usart.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmCC" --Via ".\flash\stm32f4xx_usart.__i"
"C:\Keil_v5\ARM\ARMCC\Bin\ArmLink" --Via ".\Flash\Blinky.lnp"
