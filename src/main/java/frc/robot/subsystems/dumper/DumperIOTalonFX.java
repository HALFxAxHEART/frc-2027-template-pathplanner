// ============================================================================
//  DumperIOTalonFX.java  -  REAL dumper (a Kraken/TalonFX tipping a bucket)
// ----------------------------------------------------------------------------
//  STUDENTS: Just an arm by another name -- a motor holds the bucket at an angle.
//  Uses gravity-cosine feedforward like the arm so the bucket holds its position.
// ============================================================================
package frc.robot.subsystems.dumper;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class DumperIOTalonFX implements DumperIO {
  private static final int kMotorCanId = 24;
  private static final CANBus kBus = CANBus.systemcore(0);
  private static final double kGearRatio = 50.0;
  private static final double kP = 30.0;
  private static final double kG = 0.3;

  private final TalonFX motor = new TalonFX(kMotorCanId, kBus);
  private final PositionVoltage positionRequest = new PositionVoltage(0).withSlot(0);
  private final VoltageOut voltageRequest = new VoltageOut(0);

  public DumperIOTalonFX() {
    var config = new TalonFXConfiguration();
    config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    config.CurrentLimits.SupplyCurrentLimit = 30;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.Slot0.kP = kP;
    config.Slot0.kG = kG;
    config.Slot0.GravityType = GravityTypeValue.Arm_Cosine;
    config.Feedback.SensorToMechanismRatio = kGearRatio;
    motor.getConfigurator().apply(config);
    motor.setPosition(0.0);
  }

  @Override
  public void updateInputs(DumperIOInputs inputs) {
    inputs.connected = motor.isConnected();
    inputs.positionRad = motor.getPosition().getValueAsDouble() * 2.0 * Math.PI;
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
