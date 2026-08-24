// ============================================================================
//  ElevatorIOTalonFX.java  -  REAL elevator (a Kraken/TalonFX driving a lift)
// ----------------------------------------------------------------------------
//  STUDENTS: Talks to a real TalonFX. The motor's built-in position controller
//  holds the elevator at a target HEIGHT. We convert between motor rotations and
//  meters using the gear ratio and the drum radius (kMetersPerMotorRotation).
//  kG (gravity feedforward) is extra voltage that constantly fights gravity so
//  the elevator doesn't sag.
// ============================================================================
package frc.robot.subsystems.elevator;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class ElevatorIOTalonFX implements ElevatorIO {
  // --- CHANGE THESE to match your robot ---
  private static final int kMotorCanId = 15;
  private static final CANBus kBus = CANBus.systemcore(0);
  private static final double kGearRatio = 9.0; // motor turns : 1 drum turn
  private static final double kDrumRadiusMeters = 0.025; // pulley/drum radius
  // One motor rotation raises the carriage by (2*pi*r / gearRatio) meters.
  private static final double kMetersPerMotorRotation =
      (2.0 * Math.PI * kDrumRadiusMeters) / kGearRatio;

  // Position PID + gravity feedforward -- tune to your elevator.
  private static final double kP = 4.0;
  private static final double kG = 0.3; // volts to hold against gravity

  private final TalonFX motor = new TalonFX(kMotorCanId, kBus);
  private final PositionVoltage positionRequest = new PositionVoltage(0).withSlot(0);
  private final VoltageOut voltageRequest = new VoltageOut(0);

  public ElevatorIOTalonFX() {
    var config = new TalonFXConfiguration();
    config.MotorOutput.NeutralMode = NeutralModeValue.Brake; // hold position when stopped
    config.CurrentLimits.SupplyCurrentLimit = 40;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.Slot0.kP = kP;
    config.Slot0.kG = kG;
    config.Slot0.GravityType = GravityTypeValue.Elevator_Static; // constant gravity pull
    motor.getConfigurator().apply(config);
    motor.setPosition(0.0); // assume we start at the bottom (height 0)
  }

  @Override
  public void updateInputs(ElevatorIOInputs inputs) {
    inputs.connected = motor.isConnected();
    inputs.positionMeters = motor.getPosition().getValueAsDouble() * kMetersPerMotorRotation;
    inputs.velocityMetersPerSec = motor.getVelocity().getValueAsDouble() * kMetersPerMotorRotation;
    inputs.appliedVolts = motor.getMotorVoltage().getValueAsDouble();
    inputs.currentAmps = motor.getStatorCurrent().getValueAsDouble();
  }

  @Override
  public void setHeight(double meters) {
    // Convert the target height (meters) into motor rotations.
    motor.setControl(positionRequest.withPosition(meters / kMetersPerMotorRotation));
  }

  @Override
  public void setVoltage(double volts) {
    motor.setControl(voltageRequest.withOutput(volts));
  }

  @Override
  public void stop() {
    motor.stopMotor();
  }
}
