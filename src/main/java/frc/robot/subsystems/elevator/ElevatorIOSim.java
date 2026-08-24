// ============================================================================
//  ElevatorIOSim.java  -  SIMULATION elevator (physics model, no real motor)
// ----------------------------------------------------------------------------
//  STUDENTS: WPILib's ElevatorSim models a real elevator including GRAVITY, so in
//  simulation the carriage actually falls if you don't hold it up. A simple
//  PID + gravity feedforward drives it to the target height.
// ============================================================================
package frc.robot.subsystems.elevator;

import org.wpilib.math.system.plant.DCMotor;
import org.wpilib.wpilibj.simulation.ElevatorSim;

public class ElevatorIOSim implements ElevatorIO {
  private static final double kGearRatio = 9.0;
  private static final double kDrumRadiusMeters = 0.025;
  private static final double kCarriageMassKg = 4.0;
  private static final double kMinHeight = 0.0;
  private static final double kMaxHeight = 1.5;

  private final ElevatorSim sim =
      new ElevatorSim(
          DCMotor.getKrakenX60(1),
          kGearRatio,
          kCarriageMassKg,
          kDrumRadiusMeters,
          kMinHeight,
          kMaxHeight,
          true, // simulate gravity
          0.0); // start at the bottom

  private double appliedVolts = 0.0;
  private double targetMeters = 0.0;
  private boolean closedLoop = false;

  @Override
  public void updateInputs(ElevatorIOInputs inputs) {
    if (closedLoop) {
      // Simple PID (kP) plus a constant push to counteract gravity (kG).
      double error = targetMeters - sim.getPositionMeters();
      appliedVolts = 0.3 + 40.0 * error;
    }
    appliedVolts = Math.max(-12.0, Math.min(12.0, appliedVolts));
    sim.setInputVoltage(appliedVolts);
    sim.update(0.02);

    inputs.connected = true;
    inputs.positionMeters = sim.getPositionMeters();
    inputs.velocityMetersPerSec = sim.getVelocityMetersPerSecond();
    inputs.appliedVolts = appliedVolts;
    inputs.currentAmps = sim.getCurrentDrawAmps();
  }

  @Override
  public void setHeight(double meters) {
    closedLoop = true;
    targetMeters = meters;
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
