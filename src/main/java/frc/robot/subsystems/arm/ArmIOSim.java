// ============================================================================
//  ArmIOSim.java  -  SIMULATION single-pivot arm (with gravity!)
// ----------------------------------------------------------------------------
//  STUDENTS: SingleJointedArmSim models a real arm swinging under gravity. A
//  simple PID + gravity feedforward drives it to the target angle.
// ============================================================================
package frc.robot.subsystems.arm;

import org.wpilib.math.system.plant.DCMotor;
import org.wpilib.wpilibj.simulation.SingleJointedArmSim;

public class ArmIOSim implements ArmIO {
  private static final double kGearRatio = 60.0;
  private static final double kArmLengthMeters = 0.6;
  private static final double kArmMassKg = 4.0;

  private final SingleJointedArmSim sim =
      new SingleJointedArmSim(
          DCMotor.getKrakenX60(1),
          kGearRatio,
          SingleJointedArmSim.estimateMOI(kArmLengthMeters, kArmMassKg),
          kArmLengthMeters,
          Math.toRadians(-90), // min angle
          Math.toRadians(110), // max angle
          true, // simulate gravity
          0.0); // start horizontal

  private double appliedVolts = 0.0;
  private double targetRad = 0.0;
  private boolean closedLoop = false;

  @Override
  public void updateInputs(ArmIOInputs inputs) {
    if (closedLoop) {
      double error = targetRad - sim.getAngleRads();
      // gravity feedforward (cosine) + simple proportional term
      appliedVolts = 0.4 * Math.cos(sim.getAngleRads()) + 6.0 * error;
    }
    appliedVolts = Math.max(-12.0, Math.min(12.0, appliedVolts));
    sim.setInputVoltage(appliedVolts);
    sim.update(0.02);

    inputs.connected = true;
    inputs.positionRad = sim.getAngleRads();
    inputs.velocityRadPerSec = sim.getVelocityRadPerSec();
    inputs.appliedVolts = appliedVolts;
    inputs.currentAmps = sim.getCurrentDrawAmps();
  }

  @Override
  public void setAngle(double angleRad) {
    closedLoop = true;
    targetRad = angleRad;
  }

  @Override
  public void setVoltage(double volts) {
    closedLoop = false;
    appliedVolts = volts;
  }

  @Override
  public void stop() {
    setVoltage(0.0);
  }
}
