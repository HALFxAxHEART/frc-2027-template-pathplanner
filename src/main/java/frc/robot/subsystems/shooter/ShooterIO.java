// ============================================================================
//  ShooterIO.java  -  the IO INTERFACE for the shooter flywheel
// ----------------------------------------------------------------------------
//  STUDENTS: Just like the drivetrain, every mechanism is split into an IO
//  INTERFACE (this file) and one or more IMPLEMENTATIONS (real hardware / sim).
//  This interface lists:
//    * the INPUTS we read from the shooter each loop (the @AutoLog inputs), and
//    * the OUTPUTS we can command (the methods at the bottom).
//  Splitting it this way is what lets AdvantageKit log everything and replay it,
//  and lets you run the shooter in simulation with no real motor.
// ============================================================================
package frc.robot.subsystems.shooter;

import org.littletonrobotics.junction.AutoLog;

public interface ShooterIO {
  // @AutoLog auto-generates a "ShooterIOInputsAutoLogged" class that records all
  // of these fields to the log every loop -- no extra code needed.
  @AutoLog
  public static class ShooterIOInputs {
    public boolean connected = false; // is the motor talking to us over CAN?
    public double velocityRPM = 0.0; // how fast the flywheel is actually spinning
    public double appliedVolts = 0.0; // volts currently sent to the motor
    public double currentAmps = 0.0; // current the motor is drawing
  }

  /** Read the latest sensor values into the inputs object. */
  public default void updateInputs(ShooterIOInputs inputs) {}

  /** Spin the flywheel toward a target speed (rotations per minute). */
  public default void setVelocity(double rpm) {}

  /** Send a raw voltage to the flywheel (used by tuning/characterization). */
  public default void setVoltage(double volts) {}

  /** Stop the flywheel. */
  public default void stop() {}
}
