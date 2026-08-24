// ============================================================================
//  Elevator.java  -  the elevator SUBSYSTEM (the "place" part of pick-and-place)
// ----------------------------------------------------------------------------
//  STUDENTS: This raises/lowers a carriage to preset heights so a game piece can
//  be placed at different levels. It logs its height every loop and gives you
//  commands (goToHeight, stop) to bind to buttons in RobotContainer.
//
//  The preset heights below (STOW / LOW / HIGH) are examples -- change them to
//  match your game's scoring positions.
// ============================================================================
package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.Logger;
import org.wpilib.wpilibj2.command.Command;
import org.wpilib.wpilibj2.command.SubsystemBase;

public class Elevator extends SubsystemBase {
  // Example preset heights in meters. Tune to your robot/game.
  public static final double STOW = 0.05;
  public static final double LOW = 0.6;
  public static final double HIGH = 1.4;

  private final ElevatorIO io;
  private final ElevatorIOInputsAutoLogged inputs = new ElevatorIOInputsAutoLogged();

  private static final double kToleranceMeters = 0.03;
  private double targetMeters = 0.0;

  public Elevator(ElevatorIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("Elevator", inputs);
    Logger.recordOutput("Elevator/TargetMeters", targetMeters);
    Logger.recordOutput("Elevator/AtTarget", atTarget());
  }

  /** True when the carriage has reached (roughly) its target height. */
  public boolean atTarget() {
    return Math.abs(inputs.positionMeters - targetMeters) < kToleranceMeters;
  }

  // ---- COMMANDS ----

  /** Move to a specific height (meters) and hold there. */
  public Command goToHeight(double meters) {
    return run(
        () -> {
          targetMeters = meters;
          io.setHeight(meters);
        });
  }

  /** Relax the motor (elevator will drift/settle -- brake mode still holds on real HW). */
  public Command stop() {
    return run(io::stop);
  }
}
