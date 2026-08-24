// ============================================================================
//  ElevatorIO.java  -  the IO INTERFACE for the elevator (up/down lift)
// ----------------------------------------------------------------------------
//  STUDENTS: Same IO-layer idea as everything else. This lists what we read from
//  the elevator (how high it is, how fast, its voltage/current) and what we can
//  command it to do (go to a height, hold a voltage, stop). The @AutoLog block
//  makes all the inputs get logged automatically.
// ============================================================================
package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.AutoLog;

public interface ElevatorIO {
  @AutoLog
  public static class ElevatorIOInputs {
    public boolean connected = false;
    public double positionMeters = 0.0; // how high the carriage is (meters)
    public double velocityMetersPerSec = 0.0;
    public double appliedVolts = 0.0;
    public double currentAmps = 0.0;
  }

  public default void updateInputs(ElevatorIOInputs inputs) {}

  /** Drive the elevator to a target height (meters) and hold it there. */
  public default void setHeight(double meters) {}

  /** Send a raw voltage (used for manual jogging / testing). */
  public default void setVoltage(double volts) {}

  /** Stop the elevator motor. */
  public default void stop() {}
}
