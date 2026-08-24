// ============================================================================
//  ArmJointIOTalonFX.java  -  REAL joint (one Kraken/TalonFX)
// ----------------------------------------------------------------------------
//  STUDENTS: The SAME class builds both the shoulder and the elbow -- we just
//  pass different CAN IDs and gains into the constructor. That's why the settings
//  are constructor PARAMETERS here instead of hard-coded constants.
// ============================================================================
package frc.robot.subsystems.doublearm;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class ArmJointIOTalonFX implements ArmJointIO {
  private static final CANBus kBus = CANBus.systemcore(0);
  private final TalonFX motor;
  private final PositionVoltage positionRequest = new PositionVoltage(0).withSlot(0);

  /**
   * @param canId CAN ID of this joint's motor
   * @param gearRatio motor turns per 1 joint turn
   * @param kP position gain
   * @param kG gravity feedforward (volts to hold horizontal)
   */
  public ArmJointIOTalonFX(int canId, double gearRatio, double kP, double kG) {
    motor = new TalonFX(canId, kBus);
    var config = new TalonFXConfiguration();
    config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    config.CurrentLimits.SupplyCurrentLimit = 40;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.Slot0.kP = kP;
    config.Slot0.kG = kG;
    config.Slot0.GravityType = GravityTypeValue.Arm_Cosine;
    config.Feedback.SensorToMechanismRatio = gearRatio;
    motor.getConfigurator().apply(config);
    motor.setPosition(0.0);
  }

  @Override
  public void updateInputs(ArmJointIOInputs inputs) {
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
  public void stop() {
    motor.stopMotor();
  }
}
