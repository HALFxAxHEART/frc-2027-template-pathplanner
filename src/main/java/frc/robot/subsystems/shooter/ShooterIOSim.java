// ============================================================================
//  ShooterIOSim.java  -  SIMULATION shooter (a physics model, no real motor)
// ----------------------------------------------------------------------------
//  STUDENTS: This lets you run and test the shooter on your laptop. It models a
//  small flywheel spun by one Kraken motor. A simple proportional controller
//  turns "target RPM" into a voltage, just like the real motor's controller.
// ============================================================================
package frc.robot.subsystems.shooter;

import org.wpilib.math.system.plant.DCMotor;
import org.wpilib.math.system.plant.LinearSystemId;
import org.wpilib.wpilibj.simulation.DCMotorSim;

public class ShooterIOSim implements ShooterIO {
  // One Kraken X60 motor spinning a flywheel with a small moment of inertia.
  private final DCMotorSim sim =
      new DCMotorSim(
          LinearSystemId.createDCMotorSystem(DCMotor.getKrakenX60(1), 0.004, 1.0),
          DCMotor.getKrakenX60(1));

  private double appliedVolts = 0.0;
  private double targetRpm = 0.0;
  private boolean closedLoop = false;
  private final double kV = 0.12; // must roughly match the real gains

  @Override
  public void updateInputs(ShooterIOInputs inputs) {
    // If we're chasing a target speed, compute a simple voltage to get there.
    if (closedLoop) {
      double currentRpm = sim.getAngularVelocityRPM();
      appliedVolts = kV * (targetRpm / 60.0) + 0.05 * (targetRpm - currentRpm);
    }
    appliedVolts = Math.max(-12.0, Math.min(12.0, appliedVolts)); // clamp to battery range
    sim.setInputVoltage(appliedVolts);
    sim.update(0.02); // advance the physics by one 20 ms loop

    inputs.connected = true;
    inputs.velocityRPM = sim.getAngularVelocityRPM();
    inputs.appliedVolts = appliedVolts;
    inputs.currentAmps = sim.getCurrentDrawAmps();
  }

  @Override
  public void setVelocity(double rpm) {
    closedLoop = true;
    targetRpm = rpm;
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
