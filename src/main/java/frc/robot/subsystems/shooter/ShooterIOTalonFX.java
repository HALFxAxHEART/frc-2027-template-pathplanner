// ============================================================================
//  ShooterIOTalonFX.java  -  REAL-HARDWARE shooter (a Kraken/TalonFX flywheel)
// ----------------------------------------------------------------------------
//  STUDENTS: This is the "real motor" version of the shooter IO. It talks to a
//  TalonFX (Kraken/Falcon) over CAN and spins a flywheel at a target speed using
//  the motor's built-in velocity controller.
//
//  To add a SECOND flywheel motor (a follower on the other side), create another
//  TalonFX and call `follower.setControl(new Follower(leaderId, true));` in the
//  constructor -- see the comment below.
// ============================================================================
package frc.robot.subsystems.shooter;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class ShooterIOTalonFX implements ShooterIO {
  // --- CHANGE THESE to match your robot ---
  private static final int kMotorCanId = 20; // CAN ID of the flywheel motor
  private static final CANBus kBus = CANBus.systemcore(0); // which CAN bus (see TunerConstants)
  private static final double kGearRatio = 1.0; // motor turns : 1 flywheel turn

  // Velocity PID/feedforward gains -- tune these to your shooter.
  private static final double kP = 0.1;
  private static final double kV = 0.12; // volts per rotation/second

  private final TalonFX motor = new TalonFX(kMotorCanId, kBus);
  private final VelocityVoltage velocityRequest = new VelocityVoltage(0).withSlot(0);
  private final VoltageOut voltageRequest = new VoltageOut(0);

  public ShooterIOTalonFX() {
    var config = new TalonFXConfiguration();
    config.MotorOutput.NeutralMode = NeutralModeValue.Coast; // flywheels coast, not brake
    config.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
    config.CurrentLimits.SupplyCurrentLimit = 40;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.Slot0.kP = kP;
    config.Slot0.kV = kV;
    config.Feedback.SensorToMechanismRatio = kGearRatio;
    motor.getConfigurator().apply(config);

    // To add a follower motor on the other side of the flywheel:
    //   TalonFX follower = new TalonFX(21, kBus);
    //   follower.setControl(new com.ctre.phoenix6.controls.Follower(kMotorCanId, true));
  }

  @Override
  public void updateInputs(ShooterIOInputs inputs) {
    inputs.connected = motor.isConnected();
    // getVelocity() is in rotations/second; multiply by 60 for RPM.
    inputs.velocityRPM = motor.getVelocity().getValueAsDouble() * 60.0;
    inputs.appliedVolts = motor.getMotorVoltage().getValueAsDouble();
    inputs.currentAmps = motor.getStatorCurrent().getValueAsDouble();
  }

  @Override
  public void setVelocity(double rpm) {
    // VelocityVoltage wants rotations/second, so convert from RPM.
    motor.setControl(velocityRequest.withVelocity(rpm / 60.0));
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
