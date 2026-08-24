// ============================================================================
//  DumperIO.java  -  IO INTERFACE for a "dumper" (a tipping bucket scorer)
// ----------------------------------------------------------------------------
//  STUDENTS: A dumper is the SIMPLEST scorer: a bucket on a pivot. It sits in a
//  STOW angle to hold game pieces, then rotates to a DUMP angle to tip them out.
//  No flywheel, no aiming -- just two positions. Great for a first mechanism.
// ============================================================================
package frc.robot.subsystems.dumper;

import org.littletonrobotics.junction.AutoLog;

public interface DumperIO {
  @AutoLog
  public static class DumperIOInputs {
    public boolean connected = false;
    public double positionRad = 0.0; // bucket angle
    public double appliedVolts = 0.0;
    public double currentAmps = 0.0;
  }

  public default void updateInputs(DumperIOInputs inputs) {}

  /** Rotate the bucket to a target angle (radians). */
  public default void setAngle(double angleRad) {}

  public default void setVoltage(double volts) {}

  public default void stop() {}
}
