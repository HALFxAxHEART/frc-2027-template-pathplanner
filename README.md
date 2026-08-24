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

The `main` branch is the drivetrain + vision + autonomy only. Each game mechanism
is its own branch — check out the one that matches your game. Every branch keeps
everything from `main` and adds one mechanism, wired to an **operator controller
(USB port 1)** and runnable in simulation. All heavily commented and modeled on
real FRC seasons.

**Shooter-style games:**
- **`shooter-flywheel`** — a spin-up flywheel shooter (2024 Crescendo-style).
- **`shooter-turret`** — a rotating turret that auto-aims with the Limelight, plus the flywheel (2024/2022-style).
- **`shooter-dumper`** — the simplest scorer: a tipping bucket that dumps game pieces.

**Pick-and-place games:**
- **`pickplace-elevator`** — an elevator + gripper (2025 Reefscape-style).
- **`pickplace-elevator-arm`** — an elevator with a pivoting arm on top + gripper (reach up AND out, 2025-style).
- **`pickplace-double-arm`** — a double-jointed (shoulder + elbow) arm + gripper (2023 Charged Up-style).

Switch branches in VS Code, or e.g. `git checkout shooter-turret`. Each branch's
README has its exact controls.

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
