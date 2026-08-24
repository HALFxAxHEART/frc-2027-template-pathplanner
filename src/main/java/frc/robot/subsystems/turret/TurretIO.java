// ============================================================================
//  TurretIO.java  -  IO INTERFACE for a rotating turret
// ----------------------------------------------------------------------------
//  STUDENTS: A turret spins left/right to AIM a shooter at a target without
//  turning the whole robot. Angle is in radians, 0 = pointing straight ahead.
//  Real turrets have limited travel, so watch your soft limits!
// ============================================================================
package frc.robot.subsystems.turret;

import org.littletonrobotics.junction.AutoLog;

public interface TurretIO {
  @AutoLog
  public static class TurretIOInputs {
    public boolean connected = false;
    public double positionRad = 0.0; // turret angle (0 = straight ahead)
    public double velocityRadPerSec = 0.0;
    public double appliedVolts = 0.0;
    public double currentAmps = 0.0;
  }

  public default void updateInputs(TurretIOInputs inputs) {}

  /** Aim the turret to a target angle (radians). */
  public default void setAngle(double angleRad) {}

  public default void setVoltage(double volts) {}

  public default void stop() {}
}
