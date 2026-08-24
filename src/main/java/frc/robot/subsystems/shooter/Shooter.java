// ============================================================================
//  Shooter.java  -  the shooter SUBSYSTEM (a flywheel that launches game pieces)
// ----------------------------------------------------------------------------
//  STUDENTS: A subsystem ties together the IO (hardware) and the ACTIONS you can
//  ask for. Every loop it reads the flywheel's speed and logs it. It also gives
//  you ready-made "commands" (runAtRpm, stop) that you bind to buttons in
//  RobotContainer.
//
//  A "shooter game" robot: spin the flywheel up to speed, then feed a game piece
//  into it to launch. (A feeder roller would be its own small subsystem -- try
//  adding one as an exercise, copying this file's pattern!)
// ============================================================================
package frc.robot.subsystems.shooter;

import org.littletonrobotics.junction.Logger;
import org.wpilib.wpilibj2.command.Command;
import org.wpilib.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
  private final ShooterIO io;
  private final ShooterIOInputsAutoLogged inputs = new ShooterIOInputsAutoLogged();

  // "Close enough" window (RPM) for saying the flywheel is up to speed.
  private static final double kToleranceRpm = 150.0;
  private double targetRpm = 0.0;

  public Shooter(ShooterIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    // 1. Read the hardware. 2. Log it (shows up as "Shooter/..." in AdvantageScope).
    io.updateInputs(inputs);
    Logger.processInputs("Shooter", inputs);
    Logger.recordOutput("Shooter/TargetRPM", targetRpm);
    Logger.recordOutput("Shooter/AtSpeed", atSpeed());
  }

  /** True when the flywheel is spinning close enough to its target speed. */
  public boolean atSpeed() {
    return targetRpm > 0 && Math.abs(inputs.velocityRPM - targetRpm) < kToleranceRpm;
  }

  // ---- COMMANDS (bind these to buttons in RobotContainer) ----

  /** Spin the flywheel up to the given RPM and hold it there. */
  public Command runAtRpm(double rpm) {
    return run(
        () -> {
          targetRpm = rpm;
          io.setVelocity(rpm);
        });
  }

  /** Stop the flywheel. */
  public Command stop() {
    return run(
        () -> {
          targetRpm = 0.0;
          io.stop();
        });
  }
}
