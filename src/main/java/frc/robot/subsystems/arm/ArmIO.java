// ============================================================================
//  ArmIO.java  -  IO INTERFACE for a single-pivot arm
// ----------------------------------------------------------------------------
//  STUDENTS: An "arm" is a bar on a pivot that rotates to an ANGLE (unlike the
//  elevator, which moves in a straight line). Angles here are in RADIANS, with
//  0 = arm pointing straight out (horizontal). This interface lists what we read
//  from the arm and what we command it to do. Same IO-layer idea as everything.
// ============================================================================
package frc.robot.subsystems.arm;

import org.littletonrobotics.junction.AutoLog;

public interface ArmIO {
  @AutoLog
  public static class ArmIOInputs {
    public boolean connected = false;
    public double positionRad = 0.0; // arm angle (0 = horizontal)
    public double velocityRadPerSec = 0.0;
    public double appliedVolts = 0.0;
    public double currentAmps = 0.0;
  }

  public default void updateInputs(ArmIOInputs inputs) {}

  /** Drive the arm to a target angle (radians) and hold it there. */
  public default void setAngle(double angleRad) {}

  /** Send a raw voltage (manual jog / testing). */
  public default void setVoltage(double volts) {}

  /** Stop the arm motor. */
  public default void stop() {}
}
