// ============================================================================
//  TurretIOTalonFX.java  -  REAL turret (Kraken/TalonFX)
// ----------------------------------------------------------------------------
//  STUDENTS: Like the arm, but no gravity term (a turret spins in a flat plane).
//  IMPORTANT: set SOFT LIMITS so the turret can't wind up its wires past its
//  physical travel -- see the Forward/ReverseSoftLimit lines.
// ============================================================================
package frc.robot.subsystems.turret;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class TurretIOTalonFX implements TurretIO {
  // --- CHANGE THESE to match your robot ---
  private static final int kMotorCanId = 22;
  private static final CANBus kBus = CANBus.systemcore(0);
  private static final double kGearRatio = 40.0; // motor turns : 1 turret turn
  private static final double kP = 30.0;
  // Travel limits (radians): don't let the turret spin past +/- 120 degrees.
  private static final double kMaxRad = Math.toRadians(120);

  private final TalonFX motor = new TalonFX(kMotorCanId, kBus);
  private final PositionVoltage positionRequest = new PositionVoltage(0).withSlot(0);
  private final VoltageOut voltageRequest = new VoltageOut(0);

  public TurretIOTalonFX() {
    var config = new TalonFXConfiguration();
    config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    config.CurrentLimits.SupplyCurrentLimit = 30;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.Slot0.kP = kP;
    config.Feedback.SensorToMechanismRatio = kGearRatio;
    // Soft limits keep the turret inside its safe range (values are in turret rotations).
    config.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
    config.SoftwareLimitSwitch.ForwardSoftLimitThreshold = kMaxRad / (2.0 * Math.PI);
    config.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
    config.SoftwareLimitSwitch.ReverseSoftLimitThreshold = -kMaxRad / (2.0 * Math.PI);
    motor.getConfigurator().apply(config);
    motor.setPosition(0.0); // assume it starts centered
  }

  @Override
  public void updateInputs(TurretIOInputs inputs) {
    inputs.connected = motor.isConnected();
    inputs.positionRad = motor.getPosition().getValueAsDouble() * 2.0 * Math.PI;
    inputs.velocityRadPerSec = motor.getVelocity().getValueAsDouble() * 2.0 * Math.PI;
    inputs.appliedVolts = motor.getMotorVoltage().getValueAsDouble();
    inputs.currentAmps = motor.getStatorCurrent().getValueAsDouble();
  }

  @Override
  public void setAngle(double angleRad) {
    motor.setControl(positionRequest.withPosition(angleRad / (2.0 * Math.PI)));
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
