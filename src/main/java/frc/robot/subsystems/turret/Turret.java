// ============================================================================
//  Turret.java  -  the turret SUBSYSTEM (aims a shooter left/right)
// ----------------------------------------------------------------------------
//  STUDENTS: Two ways to use a turret:
//    1) aimTo(angle)      -> point at a fixed angle.
//    2) aimWithVision(tx) -> auto-aim using the Limelight's "tx" (how far off the
//       target is). This is the classic vision-aiming demo: keep steering until
//       tx is ~0 (target centered). Pair with the flywheel shooter to auto-score.
// ============================================================================
package frc.robot.subsystems.turret;

import java.util.function.DoubleSupplier;
import org.littletonrobotics.junction.Logger;
import org.wpilib.wpilibj2.command.Command;
import org.wpilib.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {
  private final TurretIO io;
  private final TurretIOInputsAutoLogged inputs = new TurretIOInputsAutoLogged();
  private static final double kToleranceRad = Math.toRadians(2);
  private double targetRad = 0.0;

  public Turret(TurretIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("Turret", inputs);
    Logger.recordOutput("Turret/TargetRad", targetRad);
    Logger.recordOutput("Turret/OnTarget", onTarget());
  }

  public boolean onTarget() {
    return Math.abs(inputs.positionRad - targetRad) < kToleranceRad;
  }

  /** Point the turret at a fixed angle (radians). */
  public Command aimTo(double angleRad) {
    return run(
        () -> {
          targetRad = angleRad;
          io.setAngle(angleRad);
        });
  }

  /**
   * Auto-aim using the Limelight horizontal offset "tx" (degrees). Pass
   * {@code () -> vision.getTargetX(0).getDegrees()} from RobotContainer.
   */
  public Command aimWithVision(DoubleSupplier txDegrees) {
    return run(
        () -> {
          // Steer toward the target: new target = current angle minus the offset.
          targetRad = inputs.positionRad - Math.toRadians(txDegrees.getAsDouble());
          io.setAngle(targetRad);
        });
  }
}
