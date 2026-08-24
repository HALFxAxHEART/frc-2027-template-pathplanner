// ============================================================================
//  Dumper.java  -  the dumper SUBSYSTEM (tip a bucket to score)
// ----------------------------------------------------------------------------
//  STUDENTS: The whole mechanism is two commands: hold (STOW) and dump. A great
//  first mechanism because there's no tuning beyond two angles.
// ============================================================================
package frc.robot.subsystems.dumper;

import org.littletonrobotics.junction.Logger;
import org.wpilib.wpilibj2.command.Command;
import org.wpilib.wpilibj2.command.SubsystemBase;

public class Dumper extends SubsystemBase {
  public static final double STOW = Math.toRadians(5); // holds pieces in
  public static final double DUMP = Math.toRadians(100); // tips them out

  private final DumperIO io;
  private final DumperIOInputsAutoLogged inputs = new DumperIOInputsAutoLogged();
  private double targetRad = STOW;

  public Dumper(DumperIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("Dumper", inputs);
    Logger.recordOutput("Dumper/TargetRad", targetRad);
  }

  /** Hold the bucket level to keep game pieces inside. */
  public Command stow() {
    return run(
        () -> {
          targetRad = STOW;
          io.setAngle(STOW);
        });
  }

  /** Tip the bucket to dump game pieces out (hold the button). */
  public Command dump() {
    return run(
        () -> {
          targetRad = DUMP;
          io.setAngle(DUMP);
        });
  }
}
