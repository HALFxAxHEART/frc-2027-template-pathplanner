// ============================================================================
//  GripperIOTalonFX.java  -  REAL gripper (a Kraken/TalonFX spinning rollers)
// ----------------------------------------------------------------------------
//  STUDENTS: The simplest kind of motor control -- just apply a voltage to spin
//  the rollers one way (intake) or the other (eject). No PID needed.
// ============================================================================
package frc.robot.subsystems.gripper;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class GripperIOTalonFX implements GripperIO {
  // --- CHANGE THESE to match your robot ---
  private static final int kMotorCanId = 17;
  private static final CANBus kBus = CANBus.systemcore(0);

  private final TalonFX motor = new TalonFX(kMotorCanId, kBus);
  private final VoltageOut voltageRequest = new VoltageOut(0);

  public GripperIOTalonFX() {
    var config = new TalonFXConfiguration();
    config.MotorOutput.NeutralMode = NeutralModeValue.Brake; // hold the piece when stopped
    config.CurrentLimits.SupplyCurrentLimit = 30;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    motor.getConfigurator().apply(config);
  }

  @Override
  public void updateInputs(GripperIOInputs inputs) {
    inputs.connected = motor.isConnected();
    inputs.appliedVolts = motor.getMotorVoltage().getValueAsDouble();
    inputs.currentAmps = motor.getStatorCurrent().getValueAsDouble();
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
