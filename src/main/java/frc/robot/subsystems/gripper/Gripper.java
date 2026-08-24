// ============================================================================
//  Gripper.java  -  the gripper SUBSYSTEM (the "pick" part of pick-and-place)
// ----------------------------------------------------------------------------
//  STUDENTS: Gives you ready-made commands to grab (intake), release (eject),
//  and hold a game piece. Bind these to buttons in RobotContainer. It logs the
//  roller voltage/current every loop so you can see it in AdvantageScope.
// ============================================================================
package frc.robot.subsystems.gripper;

import org.littletonrobotics.junction.Logger;
import org.wpilib.wpilibj2.command.Command;
import org.wpilib.wpilibj2.command.SubsystemBase;

public class Gripper extends SubsystemBase {
  // Example roller voltages. Tune to your rollers/game piece.
  private static final double kIntakeVolts = 6.0;
  private static final double kEjectVolts = -8.0;

  private final GripperIO io;
  private final GripperIOInputsAutoLogged inputs = new GripperIOInputsAutoLogged();

  public Gripper(GripperIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("Gripper", inputs);
  }

  // ---- COMMANDS (each runs while its button is held, then stops) ----

  /** Spin rollers inward to grab a piece (hold the button). */
  public Command intake() {
    return runEnd(() -> io.setVoltage(kIntakeVolts), io::stop);
  }

  /** Spin rollers outward to release a piece (hold the button). */
  public Command eject() {
    return runEnd(() -> io.setVoltage(kEjectVolts), io::stop);
  }
}
