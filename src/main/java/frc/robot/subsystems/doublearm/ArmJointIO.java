// ============================================================================
//  ArmJointIO.java  -  IO INTERFACE for ONE joint of the double-jointed arm
// ----------------------------------------------------------------------------
//  STUDENTS: A "double-jointed arm" (2023 Charged Up style) is really just TWO
//  single joints stacked: a SHOULDER and an ELBOW. Rather than write the joint
//  code twice, we write ONE joint here and create two of them. This is exactly
//  the same IO pattern as the drivetrain modules -- reuse instead of copy/paste!
// ============================================================================
package frc.robot.subsystems.doublearm;

import org.littletonrobotics.junction.AutoLog;

public interface ArmJointIO {
  @AutoLog
  public static class ArmJointIOInputs {
    public boolean connected = false;
    public double positionRad = 0.0;
    public double velocityRadPerSec = 0.0;
    public double appliedVolts = 0.0;
    public double currentAmps = 0.0;
  }

  public default void updateInputs(ArmJointIOInputs inputs) {}

  public default void setAngle(double angleRad) {}

  public default void stop() {}
}
