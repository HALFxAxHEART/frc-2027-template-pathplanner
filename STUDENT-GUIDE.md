# Student Guide — how this robot code works

This project has two big ideas. Once you get these, the rest is details.

---

## Idea 1: Command-based robots

WPILib organizes robot code into **subsystems** and **commands**.

- A **subsystem** is a piece of hardware you control as a unit. Here we have one:
  **`Drive`** (the swerve drivetrain — 4 wheels + a gyro).
- A **command** is an action that uses subsystems. Example: `joystickDrive` reads
  the sticks and tells `Drive` where to go.

A background **Command Scheduler** runs ~50 times per second. Each tick it:
1. checks the controller buttons,
2. runs whatever commands are active,
3. calls each subsystem's `periodic()` method.

That scheduler is started by the single line `CommandScheduler.getInstance().run()`
in `Robot.robotPeriodic()`. `RobotContainer` is where we say *which button does
what* and *what the default action is* (drive with the sticks).

---

## Idea 2: AdvantageKit and the "IO layer" — this is why we can log & replay

Normally robot code talks straight to motors. AdvantageKit adds a thin **layer**
in between, and that layer is the whole trick.

For each hardware thing, there are three pieces:

```
        ModuleIO            (an INTERFACE: "here are the inputs and outputs")
       /        \
ModuleIOTalonFX   ModuleIOSim   (two IMPLEMENTATIONS of that interface)
 (real motors)    (physics sim)
```

- **`ModuleIO`** just lists the numbers we read (wheel position, speed, motor
  voltage, current, temperature…) and the commands we can send.
- **`ModuleIOTalonFX`** is the real version — it talks to the Kraken motors.
- **`ModuleIOSim`** is a pretend version — a little physics model, no hardware.

The `Drive`/`Module` code only ever uses the **interface**, so it doesn't know or
care whether it got real motors or a simulation. `RobotContainer` decides which
one to plug in, based on the mode:

| Mode | When | Gyro / Modules used | What you get |
|---|---|---|---|
| **REAL** | on the actual robot | `GyroIOPigeon2` + `ModuleIOTalonFX` | drives real motors, saves a log |
| **SIM** | on your laptop | empty gyro + `ModuleIOSim` | drive in simulation, no hardware |
| **REPLAY** | re-running a log | empty IO | re-runs your code against a saved log |

**Every input the IO layer reads is recorded to the log.** That's what "log
everything" means, and it's what makes **replay** possible: later you can open an
old log, add a new logged value or a new calculation, and re-run your exact code
as if the robot were live again. That's a superpower for debugging.

---

## What actually gets logged?

- Every field in the `…IOInputs` classes (positions, speeds, voltages, currents,
  gyro angle, odometry timestamps) — automatically, thanks to the `@AutoLog`
  annotation.
- The robot's estimated **pose** (where it is on the field) and each swerve
  module's target vs. actual state.
- Anything you add yourself with `Logger.recordOutput("MyName", value)`.
- Joystick inputs, enabled/disabled state, battery voltage, loop timing, CAN
  stats — AdvantageKit grabs these for free.

You **view** all of it in **AdvantageScope**: line graphs, a 2D field, a 3D field,
swerve visualizations, and more. Connect live to the robot, or open a `.wpilog`.

---

## Where the robot's real numbers live

`generated/TunerConstants.java` is the one file that describes *this specific
robot*: the CAN ID of every motor and encoder, the gear ratios, the wheel size,
the top speed, the tuning gains, and where each wheel sits. These came from the
real Stingray robot. When something physical is wrong (a wheel spins backwards, a
module points 90° off, the robot mis-measures distance), the fix is almost always
a number in that file.

---

## Try this (great first exercises)

1. **Run the simulation** (README Step 6) and drive around while watching the
   field view in AdvantageScope. No robot required.
2. **Log a new value:** in `Drive.java`'s `periodic()`, add
   `Logger.recordOutput("Drive/MySpeed", getFFCharacterizationVelocity());`
   redeploy, and find `Drive/MySpeed` in AdvantageScope.
3. **Change a control gain** in `TunerConstants.java` (e.g. steer `kP`) in
   simulation and watch how the modules respond differently.

Ask questions freely — this is alpha software and learning it *is* the project.
