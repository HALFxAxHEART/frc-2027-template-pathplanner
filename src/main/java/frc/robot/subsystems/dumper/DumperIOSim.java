// ============================================================================
//  DumperIOSim.java  -  SIMULATION dumper (a pivoting bucket, with gravity)
// ============================================================================
package frc.robot.subsystems.dumper;

import org.wpilib.math.system.plant.DCMotor;
import org.wpilib.wpilibj.simulation.SingleJointedArmSim;

public class DumperIOSim implements DumperIO {
  private final SingleJointedArmSim sim =
      new SingleJointedArmSim(
          DCMotor.getKrakenX60(1),
          50.0,
          SingleJointedArmSim.estimateMOI(0.3, 3.0),
          0.3,
          Math.toRadians(-10),
          Math.toRadians(110),
          true,
          0.0);

  private double appliedVolts = 0.0;
  private double targetRad = 0.0;
  private boolean closedLoop = false;

  @Override
  public void updateInputs(DumperIOInputs inputs) {
    if (closedLoop) {
      double error = targetRad - sim.getAngleRads();
      appliedVolts = 0.3 * Math.cos(sim.getAngleRads()) + 5.0 * error;
    }
    appliedVolts = Math.max(-12.0, Math.min(12.0, appliedVolts));
    sim.setInputVoltage(appliedVolts);
    sim.update(0.02);
    inputs.connected = true;
    inputs.positionRad = sim.getAngleRads();
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
