package io.github.wsyong11.gameforge.framework.system.crashreport;

/*
---------- CRASH REPORT ----------
Time: {$TIME}
Description: ${DESCRIPTION}

${STACK_TRACE}


A detailed walkthrough of the error, its code path and all known details is as follows:
============= DETAILS ==============
--- Thread ${THREAD_NAME} ---
Status: SLEEPING
Stack track:
    ${STACK_TRACE}


========== SYSTEM DETAILS ==========
OS: ${OS_NAME} ${ARCH} ${OS_VERSION} ${OS_BUILD_VERSION}
Language: ${SYSTEM_LANGUAGE}
Region: ${REGION}
Time Zone: {SYSTEM_TIME_ZONE}
Uptime: DD:HH:MM:SS.sss

========== HARDWARE DETAILS ==========
--- CPU ---
Model: ${CPU_MODEL}
Cores: ${CPU_CORE_COUNT}
Frequency: ${CPU_FREQUENCY}

--- Memory ---
Total size: {FORMATTED_MEMORY_SIZE}
Available Memory: {$AVAILABLE_MEMORY$}
Swap Total: {$SWAP_TOTAL$}
Swap Used: {$SWAP_USED$}


----------------------------------
 */

/*
ChatGPT
---------- CRASH REPORT ----------
Time: {$TIME$}
Error ID: {$ERROR_ID$}
Description: {$DESCRIPTION$}

============= THREAD INFO =============
Crashed Thread: {$THREAD_NAME$}
Thread Status: {$THREAD_STATUS$}
Thread Priority: {$THREAD_PRIORITY$}
Stack Trace Summary:
{$STACK_TRACE_SUMMARY$}

Full Stack Trace:
{$STACK_TRACE$}

============= SYSTEM INFO =============
--- Operating System ---
OS Name: {$OS_NAME$}
OS Version: {$OS_VERSION$}
OS Build/Kernel: {$OS_BUILD$}
System Architecture: {$ARCHITECTURE$}
System Language: {$SYSTEM_LANGUAGE$}
Time Zone: {$TIME_ZONE$}
System Uptime: {$SYSTEM_UPTIME$}

--- CPU ---
CPU Model: {$CPU_MODEL$}
Cores: {$CPU_CORES$}
Threads: {$CPU_THREADS$}
Current Frequency: {$CPU_FREQ$}
Max Frequency: {$CPU_MAX_FREQ$}

--- Memory ---
Total Memory: {$TOTAL_MEMORY$}
Available Memory: {$AVAILABLE_MEMORY$}
Swap Total: {$SWAP_TOTAL$}
Swap Used: {$SWAP_USED$}

--- Storage ---
Disk List:
{$DISK_LIST$}  # 可以列每个盘符/分区/容量/文件系统
Open File Handles: {$OPEN_FILE_HANDLES$}

--- GPU (if applicable) ---
GPU Model: {$GPU_MODEL$}
GPU Memory: {$GPU_MEMORY$}
Driver Version: {$GPU_DRIVER$}

--- Network ---
Hostname: {$HOSTNAME$}
IP Addresses: {$IP_LIST$}
MAC Addresses: {$MAC_LIST$}
Default Gateway: {$DEFAULT_GATEWAY$}
DNS Servers: {$DNS_SERVERS$}
Network Status: {$NETWORK_STATUS$}

--- User & Security ---
Current User: {$CURRENT_USER$}
User Groups: {$USER_GROUPS$}
Privileges: {$USER_PRIVILEGES$}
Firewall Status: {$FIREWALL_STATUS$}
Antivirus/Security Software: {$SECURITY_SOFTWARE$}
SELinux/AppArmor Status: {$SECURITY_MODULE_STATUS$} # Linux

--- Runtime Environment ---
Programming Language/Runtime: {$RUNTIME_NAME$} {$RUNTIME_VERSION$}
Dependencies & Versions: {$DEPENDENCIES_LIST$}
Environment Variables: {$ENV_VARIABLES$}
Current Working Directory: {$CWD$}

--- Peripheral Devices ---
USB Devices: {$USB_DEVICES$}
External Drives: {$EXTERNAL_DRIVES$}
Other Devices: {$OTHER_DEVICES$}

============= ADVANCED INFO =============
Recent System Logs: {$RECENT_LOGS$}
Heap/Memory Dump Info: {$MEMORY_DUMP$}  # 如果有
Crash Trigger Function/Module: {$CRASH_MODULE$}
Additional Notes: {$ADDITIONAL_NOTES$}

----------------------------------------
 */

import io.github.wsyong11.gameforge.framework.system.crashreport.detail.CrashReportDetail;
import org.jetbrains.annotations.NotNull;

public interface CrashReport {
	@NotNull
	Throwable getException();

	@NotNull
	String getDescription();

	@NotNull
	CrashReport addDetail(@NotNull CrashReportDetail detail);
}
