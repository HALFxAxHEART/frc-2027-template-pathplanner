// ============================================================================
//  TurretIOSim.java  -  SIMULATION turret (flat spinning mass, no gravity)
// ============================================================================
package frc.robot.subsystems.turret;

import org.wpilib.math.system.plant.DCMotor;
import org.wpilib.math.system.plant.LinearSystemId;
import org.wpilib.wpilibj.simulation.DCMotorSim;

public class TurretIOSim implements TurretIO {
  private final DCMotorSim sim =
      new DCMotorSim(
          LinearSystemId.createDCMotorSystem(DCMotor.getKrakenX60(1), 0.02, 40.0),
          DCMotor.getKrakenX60(1));

  private double appliedVolts = 0.0;
  private double targetRad = 0.0;
  private boolean closedLoop = false;

  @Override
  public void updateInputs(TurretIOInputs inputs) {
    if (closedLoop) {
      double error = targetRad - sim.getAngularPositionRad();
      appliedVolts = 4.0 * error; // simple proportional aim
    }
    appliedVolts = Math.max(-12.0, Math.min(12.0, appliedVolts));
    sim.setInputVoltage(appliedVolts);
    sim.update(0.02);

    inputs.connected = true;
    inputs.positionRad = sim.getAngularPositionRad();
    inputs.velocityRadPerSec = sim.getAngularVelocityRadPerSec();
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
