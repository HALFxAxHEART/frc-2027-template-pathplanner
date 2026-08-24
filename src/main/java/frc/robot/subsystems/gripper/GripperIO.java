// ============================================================================
//  GripperIO.java  -  the IO INTERFACE for the gripper (intake rollers)
// ----------------------------------------------------------------------------
//  STUDENTS: The gripper grabs and releases game pieces with spinning rollers.
//  This interface lists what we read (voltage, current -- a spike in current can
//  tell you a piece is grabbed) and what we command (run at a voltage, stop).
// ============================================================================
package frc.robot.subsystems.gripper;

import org.littletonrobotics.junction.AutoLog;

public interface GripperIO {
  @AutoLog
  public static class GripperIOInputs {
    public boolean connected = false;
    public double appliedVolts = 0.0;
    public double currentAmps = 0.0; // watch this: high current often = holding a piece
  }

  public default void updateInputs(GripperIOInputs inputs) {}

  /** Run the rollers at a voltage. Positive = intake, negative = eject (by convention). */
  public default void setVoltage(double volts) {}

  /** Stop the rollers. */
  public default void stop() {}
}
