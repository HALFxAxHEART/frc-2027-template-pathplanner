// ============================================================================
//  Arm.java  -  the single-pivot arm SUBSYSTEM
// ----------------------------------------------------------------------------
//  STUDENTS: Rotates an arm to preset angles (STOW / SCORE / INTAKE). Pair it
//  with a gripper on the end to make a pick-and-place robot (2025 Reefscape
//  style). Logs its angle every loop for AdvantageScope.
// ============================================================================
package frc.robot.subsystems.arm;

import org.littletonrobotics.junction.Logger;
import org.wpilib.wpilibj2.command.Command;
import org.wpilib.wpilibj2.command.SubsystemBase;

public class Arm extends SubsystemBase {
  // Example preset angles in RADIANS. Tune to your robot/game.
  public static final double STOW = Math.toRadians(90); // arm up
  public static final double SCORE = Math.toRadians(30);
  public static final double INTAKE = Math.toRadians(-20); // arm down to pick up

  private final ArmIO io;
  private final ArmIOInputsAutoLogged inputs = new ArmIOInputsAutoLogged();
  private static final double kToleranceRad = Math.toRadians(3);
  private double targetRad = 0.0;

  public Arm(ArmIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("Arm", inputs);
    Logger.recordOutput("Arm/TargetRad", targetRad);
    Logger.recordOutput("Arm/AtTarget", atTarget());
  }

  public boolean atTarget() {
    return Math.abs(inputs.positionRad - targetRad) < kToleranceRad;
  }

  /** Move to a target angle (radians) and hold. */
  public Command goToAngle(double angleRad) {
    return run(
        () -> {
          targetRad = angleRad;
          io.setAngle(angleRad);
        });
  }
}
