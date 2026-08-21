// ============================================================================
//  RobotContainer.java  -  wires the robot together
// ----------------------------------------------------------------------------
//  STUDENTS: This is the "assembly point" of the robot. It does three jobs:
//
//    1. CREATE the drivetrain subsystem -- and here is the AdvantageKit magic:
//       we build the SAME "Drive" subsystem three different ways depending on
//       the mode:
//         REAL   -> real hardware  (GyroIOPigeon2 + ModuleIOTalonFX x4)
//         SIM    -> physics sim    (GyroIO{} placeholder + ModuleIOSim x4)
//         REPLAY -> do nothing     (empty IO objects; values come from the log)
//       The Drive subsystem itself doesn't care which one it got -- that's the
//       whole point of the "IO layer" pattern. It makes code testable off-robot.
//
//    2. SET UP the autonomous chooser (a dropdown on the dashboard).
//
//    3. BIND the driver's controller buttons to robot actions.
//
//  The actual robot numbers (CAN IDs, wheel size, gear ratios, tuning gains)
//  live in generated/TunerConstants.java -- those came from your Stingray robot.
// ============================================================================
package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import frc.robot.commands.DriveCommands;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.GyroIO;
import frc.robot.subsystems.drive.GyroIOPigeon2;
import frc.robot.subsystems.drive.ModuleIO;
import frc.robot.subsystems.drive.ModuleIOSim;
import frc.robot.subsystems.drive.ModuleIOTalonFX;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionConstants;
import frc.robot.subsystems.vision.VisionIO;
import frc.robot.subsystems.vision.VisionIOLimelight;
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.wpilibj2.command.Command;
import org.wpilib.wpilibj2.command.Commands;
import org.wpilib.wpilibj2.command.button.CommandXboxController;
import org.wpilib.wpilibj2.command.sysid.SysIdRoutine;

public class RobotContainer {
  // The swerve drivetrain subsystem.
  private final Drive drive;

  // The vision subsystem: two Limelight cameras that help the drivetrain know
  // where it is on the field (see subsystems/vision/).
  private final Vision vision;

  // The driver's Xbox controller, plugged into USB port 0 in the Driver Station.
  private final CommandXboxController controller = new CommandXboxController(0);

  // The autonomous mode chooser (shows up as a dropdown in AdvantageScope/Elastic).
  private final LoggedDashboardChooser<Command> autoChooser;

  public RobotContainer() {
    // ---- 1. Build the drivetrain in the right way for the current mode ----
    switch (Constants.currentMode) {
      case REAL:
        // Real robot: talk to the real Pigeon2 gyro and the four real swerve
        // modules (each = 2 Kraken/TalonFX motors + 1 CANcoder).
        drive =
            new Drive(
                new GyroIOPigeon2(),
                new ModuleIOTalonFX(TunerConstants.FrontLeft),
                new ModuleIOTalonFX(TunerConstants.FrontRight),
                new ModuleIOTalonFX(TunerConstants.BackLeft),
                new ModuleIOTalonFX(TunerConstants.BackRight));
        break;

      case SIM:
        // Simulation: no gyro hardware, and each module is a physics model.
        drive =
            new Drive(
                new GyroIO() {},
                new ModuleIOSim(TunerConstants.FrontLeft),
                new ModuleIOSim(TunerConstants.FrontRight),
                new ModuleIOSim(TunerConstants.BackLeft),
                new ModuleIOSim(TunerConstants.BackRight));
        break;

      default:
        // Replay: empty IO. The real values are supplied by the log file.
        drive =
            new Drive(
                new GyroIO() {},
                new ModuleIO() {},
                new ModuleIO() {},
                new ModuleIO() {},
                new ModuleIO() {});
        break;
    }

    // ---- 1b. Vision: hook two Limelights into the drivetrain's pose estimator ----
    // Vision calls drive.addVisionMeasurement(...) whenever it sees AprilTags, and
    // reads drive.getRotation() to help MegaTag2. Camera names come from
    // VisionConstants (they must match the Limelight web-UI names!).
    if (Constants.currentMode == Constants.Mode.REPLAY) {
      // Replay: empty cameras -- the real data comes from the log.
      vision = new Vision(drive::addVisionMeasurement, new VisionIO() {}, new VisionIO() {});
    } else {
      // Real robot (and sim): read the two Limelights over NetworkTables.
      // camera0 = Limelight 3/2, camera1 = USB cam on the SystemCore.
      vision =
          new Vision(
              drive::addVisionMeasurement,
              new VisionIOLimelight(VisionConstants.camera0Name, drive::getRotation),
              new VisionIOLimelight(VisionConstants.camera1Name, drive::getRotation));
    }

    // ---- 2. Autonomous chooser ----
    // AutoBuilder.buildAutoChooser() finds any PathPlanner autos in
    // src/main/deploy/pathplanner and lists them. (Requires the PathPlannerLib
    // vendordep -- see the README for how to add it.)
    autoChooser = new LoggedDashboardChooser<>("Auto Choices", AutoBuilder.buildAutoChooser());

    // Extra built-in "characterization" routines. These drive the robot in
    // controlled ways to MEASURE things (like real wheel radius or motor
    // constants). Handy, but you can ignore them while learning.
    autoChooser.addOption(
        "Drive Wheel Radius Characterization", DriveCommands.wheelRadiusCharacterization(drive));
    autoChooser.addOption(
        "Drive Simple FF Characterization", DriveCommands.feedforwardCharacterization(drive));
    autoChooser.addOption(
        "Drive SysId (Quasistatic Forward)",
        drive.sysIdQuasistatic(SysIdRoutine.Direction.kForward));
    autoChooser.addOption(
        "Drive SysId (Quasistatic Reverse)",
        drive.sysIdQuasistatic(SysIdRoutine.Direction.kReverse));
    autoChooser.addOption(
        "Drive SysId (Dynamic Forward)", drive.sysIdDynamic(SysIdRoutine.Direction.kForward));
    autoChooser.addOption(
        "Drive SysId (Dynamic Reverse)", drive.sysIdDynamic(SysIdRoutine.Direction.kReverse));

    // ---- 3. Controller buttons ----
    configureButtonBindings();
  }

  /** Maps the driver's controller sticks and buttons to robot actions. */
  private void configureButtonBindings() {
    // DEFAULT COMMAND: whenever nothing else is using the drivetrain, drive it
    // with the joysticks. This is "field-relative" driving: push the stick "away"
    // and the robot goes away from you, no matter which way it is facing.
    //   Left stick Y  -> forward/back      (negated so "up" = forward)
    //   Left stick X  -> left/right strafe (negated so "left" = left)
    //   Right stick X -> rotate            (negated so "left" = turn left)
    drive.setDefaultCommand(
        DriveCommands.joystickDrive(
            drive,
            () -> -controller.getLeftY(),
            () -> -controller.getLeftX(),
            () -> -controller.getRightX()));

    // Hold A: point/lock the robot's heading toward 0 degrees while still driving.
    controller
        .a()
        .whileTrue(
            DriveCommands.joystickDriveAtAngle(
                drive,
                () -> -controller.getLeftY(),
                () -> -controller.getLeftX(),
                () -> Rotation2d.kZero));

    // Press X: turn the wheels into an "X" shape to lock the robot in place.
    controller.x().onTrue(Commands.runOnce(drive::stopWithX, drive));

    // Press B: tell the robot "you are currently facing forward" (reset the gyro).
    // Useful if field-relative driving gets rotated. Works even while disabled.
    controller
        .b()
        .onTrue(
            Commands.runOnce(
                    () ->
                        drive.setPose(
                            new Pose2d(drive.getPose().getTranslation(), Rotation2d.kZero)),
                    drive)
                .ignoringDisable(true));
  }

  /** Robot.java calls this to get the command to run during autonomous. */
  public Command getAutonomousCommand() {
    return autoChooser.get();
  }
}
