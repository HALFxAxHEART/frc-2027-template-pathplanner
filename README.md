# FRC 2027 Swerve Template — PathPlanner + Limelight + AdvantageKit

A **teaching template** for WPILib **2027** on the **SystemCore** (or the
Raspberry Pi "SystemCore clone"). It gives students a complete, working starting
point:

- **Swerve drivetrain** (CTRE Phoenix 6) built on AdvantageKit's official
  `talonfx-swerve` template.
- **AdvantageKit logging** — *everything* is logged and viewable live or in
  replay with **AdvantageScope**.
- **Limelight vision** — two cameras feeding AprilTag pose estimates into the
  drivetrain (one Limelight 3/2 + the USB camera on the SystemCore).
- **PathPlanner** for autonomous path following.
- Heavy comments throughout so students can follow what each part does.

> There is a sibling template that uses **Choreo** instead of PathPlanner:
> `frc-2027-template-choreo`. Same everything else.

## Mechanism branches (game-specific robots)

The `main` branch is just the drivetrain + vision + autonomy. Game mechanisms
live on their own branches so students can check out the one for their game:

- **`shooter`** — adds a flywheel shooter (a "shooter game" robot).
- **`pick-and-place`** — adds an elevator + gripper (a "pick-and-place" robot).

*(More mechanism archetypes — arm-on-elevator, double-jointed arm, turret, etc. —
are being added; see the repo branches.)*

Switch branches in VS Code, or: `git checkout shooter`.

---

## ⚠️ Read this first — one honest heads-up

WPILib 2027 is **alpha software** and its Java package names shifted between
alpha releases. This is assembled from the official templates, but it was **not
compiled in a 2027 environment**. Expect **one "import + build" pass in WPILib
VS Code** to finish the namespace migration (WPILib's importer does most of it).
If anything won't compile, send the exact error and it can be fixed quickly.

---

## Quick start

1. **Copy** this folder locally and open it in **WPILib 2027 VS Code**
   (*File → Open Folder*).
2. **Let the importer run** if prompted (it updates package names to your exact
   2027 build).
3. **Install vendordeps** via *WPILib: Manage Vendor Libraries → Install new
   libraries (online)*:
   - AdvantageKit: `https://github.com/Mechanical-Advantage/AdvantageKit/releases/download/v27.0.0-alpha-4/AdvantageKit.json`
   - **CTRE-Phoenix (v6)** and **PathPlannerLib** from the vendordep list.
4. **Set your CAN bus** in `src/main/java/frc/robot/generated/TunerConstants.java`
   (`kCANBus`): `CANBus.systemcore(0)` for the built-in HAT, or a CANivore name.
5. **Set your Limelight names** in
   `src/main/java/frc/robot/subsystems/vision/VisionConstants.java`
   (`camera0Name`, `camera1Name`) to match the Limelight web UI.
6. **Try SIMULATION first** (no robot needed): *WPILib: Simulate Robot Code*,
   then connect AdvantageScope to the simulator.
7. **Deploy** to the SystemCore and enable from the Driver Station.

## Driver controls (Xbox controller, port 0)

| Control | Action |
|---|---|
| Left stick | Drive (field-relative) |
| Right stick X | Rotate |
| Hold A | Drive while locking heading to 0° |
| Press X | Lock wheels in an "X" |
| Press B | Reset gyro "forward" |

---

## What's inside

```
src/main/java/frc/robot/
├── Robot.java              main loop + AdvantageKit logging ("log everything")
├── Constants.java          REAL / SIM / REPLAY mode switch
├── RobotContainer.java     wiring: drivetrain + vision + buttons
├── generated/TunerConstants.java   drivetrain numbers (CAN IDs, gains, sizes)
├── commands/DriveCommands.java     joystick driving + calibration
├── subsystems/drive/       swerve subsystem (IO-layer: real + sim + replay)
├── subsystems/vision/      two Limelight cameras -> pose estimates
└── util/                   Phoenix + PathPlanner helpers
```

See **STUDENT-GUIDE.md** for the concepts (command-based robots, the AdvantageKit
IO-layer pattern, and REAL/SIM/REPLAY).

> The drivetrain numbers in `TunerConstants.java` are a working example from a
> real robot (Team 5090's "Stingray"). Replace them with your robot's values —
> or regenerate the file with CTRE's Tuner X swerve generator.


---

## This branch: `shooter-flywheel`

Adds a **flywheel shooter** (2024 Crescendo-style). Operator controller (USB port 1): hold **Right Bumper** to spin up to 3000 RPM; release to stop.

Everything from `main` (swerve + Limelight vision + autonomy + logging) is
still here; this branch just adds the mechanism above. Run it in simulation
(*WPILib: Simulate Robot Code*) and watch the `Shooter/...`
values in AdvantageScope.
