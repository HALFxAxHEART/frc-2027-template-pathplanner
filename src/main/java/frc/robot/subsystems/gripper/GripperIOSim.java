// ============================================================================
//  GripperIOSim.java  -  SIMULATION gripper (a simple spinning-roller model)
// ----------------------------------------------------------------------------
//  STUDENTS: A tiny physics model so the gripper "works" in simulation. It does
//  not model actually grabbing a piece -- just the motor spinning.
// ============================================================================
package frc.robot.subsystems.gripper;

import org.wpilib.math.system.plant.DCMotor;
import org.wpilib.math.system.plant.LinearSystemId;
import org.wpilib.wpilibj.simulation.DCMotorSim;

public class GripperIOSim implements GripperIO {
  private final DCMotorSim sim =
      new DCMotorSim(
          LinearSystemId.createDCMotorSystem(DCMotor.getKrakenX60(1), 0.002, 5.0),
          DCMotor.getKrakenX60(1));

  private double appliedVolts = 0.0;

  @Override
  public void updateInputs(GripperIOInputs inputs) {
    appliedVolts = Math.max(-12.0, Math.min(12.0, appliedVolts));
    sim.setInputVoltage(appliedVolts);
    sim.update(0.02);
    inputs.connected = true;
    inputs.appliedVolts = appliedVolts;
    inputs.currentAmps = sim.getCurrentDrawAmps();
  }

  @Override
  public void setVoltage(double volts) {
    appliedVolts = volts;
  }

  @Override
  public void stop() {
    appliedVolts = 0.0;
  }
}
