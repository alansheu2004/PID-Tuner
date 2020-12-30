# PID-Tuner
A small Java tool to help graph and tune PID.

## PID Class
This is just a class for a PID Controller.
- Instantiate with PID(kP, kI, kD).
- Update the cycle with update(actual, setpoint), filling in the process variable and setpoint.
  - Note that it keeps track of time internally.
- Reset with reset().

## PID Tuning Window
This is the window with all of the tuning tools.
- Instantiate with PIDFrame(pid), using the PID object you want to tune.
- Graph
  - The red line is the error, green is SP, and blue is PV.
- Success Conditions
  - You can set the amount of time with a maximum error for the PID to have succeeded.
  - It will tell the time it takes for it to reach success.
- You can change the PID values with the number spinners on the bottom.
  - The PID values do not change dynamically, only changing when you press the "Update Values" button.
  - Undo any changes with "Reset Values".
  - This does not change the PID object permanently, only over the duration of the run. Alter the code afterwards.
- Log
  - Pressing "View Log" Will show a log of the different PID gain tested and the time they took for successes.
  - You can clear the log. Note that this is permanent.
  - A log file will be created in the current directory.
  
 
