// ============================================================================
//  ArmJointIOSim.java  -  SIMULATION joint (one gravity-affected arm segment)
// ============================================================================
package frc.robot.subsystems.doublearm;

import org.wpilib.math.system.plant.DCMotor;
import org.wpilib.wpilibj.simulation.SingleJointedArmSim;

public class ArmJointIOSim implements ArmJointIO {
  private final SingleJointedArmSim sim;
  private double appliedVolts = 0.0;
  private double targetRad = 0.0;
  private boolean closedLoop = false;
  private final double kG;

  /**
   * @param gearRatio motor turns per joint turn
   * @param lengthMeters length of this arm segment
   * @param massKg mass of this segment
   * @param kG gravity feedforward for the sim controller
   */
  public ArmJointIOSim(double gearRatio, double lengthMeters, double massKg, double kG) {
    this.kG = kG;
    sim =
        new SingleJointedArmSim(
            DCMotor.getKrakenX60(1),
            gearRatio,
            SingleJointedArmSim.estimateMOI(lengthMeters, massKg),
            lengthMeters,
            Math.toRadians(-160),
            Math.toRadians(160),
            true,
            0.0);
  }

  @Override
  public void updateInputs(ArmJointIOInputs inputs) {
    if (closedLoop) {
      double error = targetRad - sim.getAngleRads();
      appliedVolts = kG * Math.cos(sim.getAngleRads()) + 6.0 * error;
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
  public void stop() {
    closedLoop = false;
    appliedVolts = 0.0;
  }
}
