// ============================================================================
//  ArmIOTalonFX.java  -  REAL single-pivot arm (Kraken/TalonFX)
// ----------------------------------------------------------------------------
//  STUDENTS: The motor's built-in position controller holds the arm at a target
//  angle. kG here is "gravity cosine": an arm needs the MOST holding force when
//  horizontal and the LEAST when vertical, so Phoenix multiplies kG by the cosine
//  of the angle automatically (GravityType = Arm_Cosine).
// ============================================================================
package frc.robot.subsystems.arm;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class ArmIOTalonFX implements ArmIO {
  // --- CHANGE THESE to match your robot ---
  private static final int kMotorCanId = 11;
  private static final CANBus kBus = CANBus.systemcore(0);
  private static final double kGearRatio = 60.0; // motor turns : 1 arm turn (big reduction)

  private static final double kP = 40.0;
  private static final double kG = 0.4; // volts to hold the arm horizontal

  private final TalonFX motor = new TalonFX(kMotorCanId, kBus);
  private final PositionVoltage positionRequest = new PositionVoltage(0).withSlot(0);
  private final VoltageOut voltageRequest = new VoltageOut(0);

  public ArmIOTalonFX() {
    var config = new TalonFXConfiguration();
    config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    config.CurrentLimits.SupplyCurrentLimit = 40;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.Slot0.kP = kP;
    config.Slot0.kG = kG;
    config.Slot0.GravityType = GravityTypeValue.Arm_Cosine; // gravity depends on arm angle
    config.Feedback.SensorToMechanismRatio = kGearRatio; // report angle in ARM rotations
    motor.getConfigurator().apply(config);
    motor.setPosition(0.0); // assume arm starts horizontal; use a CANcoder for real robots
  }

  @Override
  public void updateInputs(ArmIOInputs inputs) {
    inputs.connected = motor.isConnected();
    // getPosition() is in rotations; convert to radians (2*pi per rotation).
    inputs.positionRad = motor.getPosition().getValueAsDouble() * 2.0 * Math.PI;
    inputs.velocityRadPerSec = motor.getVelocity().getValueAsDouble() * 2.0 * Math.PI;
    inputs.appliedVolts = motor.getMotorVoltage().getValueAsDouble();
    inputs.currentAmps = motor.getStatorCurrent().getValueAsDouble();
  }

  @Override
  public void setAngle(double angleRad) {
    motor.setControl(positionRequest.withPosition(angleRad / (2.0 * Math.PI))); // rad -> rotations
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
