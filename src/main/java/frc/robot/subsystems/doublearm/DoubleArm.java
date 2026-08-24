// ============================================================================
//  DoubleArm.java  -  the double-jointed arm SUBSYSTEM (shoulder + elbow)
// ----------------------------------------------------------------------------
//  STUDENTS: This one subsystem controls TWO joints together. A "pose" is a pair
//  of angles (shoulder, elbow) that puts the hand at a useful spot -- e.g. STOW
//  (tucked in), PICKUP (reach to the floor), SCORE_HIGH (reach up and out). This
//  is how 2023 Charged Up arms reached different scoring heights.
//
//  NOTE: on a real robot the two joints must move together carefully so the arm
//  doesn't smash the robot or the field -- that's called "path planning" for the
//  arm and is a great advanced topic. Here we simply command both target angles.
// ============================================================================
package frc.robot.subsystems.doublearm;

import org.littletonrobotics.junction.Logger;
import org.wpilib.wpilibj2.command.Command;
import org.wpilib.wpilibj2.command.SubsystemBase;

public class DoubleArm extends SubsystemBase {
  // A named arm pose = a (shoulder, elbow) angle pair, in radians.
  public static record Pose(double shoulderRad, double elbowRad) {}

  // Example poses -- tune to your arm geometry.
  public static final Pose STOW = new Pose(Math.toRadians(95), Math.toRadians(-120));
  public static final Pose PICKUP = new Pose(Math.toRadians(20), Math.toRadians(-30));
  public static final Pose SCORE_HIGH = new Pose(Math.toRadians(60), Math.toRadians(45));

  private final ArmJointIO shoulderIO;
  private final ArmJointIO elbowIO;
  private final ArmJointIOInputsAutoLogged shoulderInputs = new ArmJointIOInputsAutoLogged();
  private final ArmJointIOInputsAutoLogged elbowInputs = new ArmJointIOInputsAutoLogged();
  private Pose target = STOW;

  public DoubleArm(ArmJointIO shoulderIO, ArmJointIO elbowIO) {
    this.shoulderIO = shoulderIO;
    this.elbowIO = elbowIO;
  }

  @Override
  public void periodic() {
    shoulderIO.updateInputs(shoulderInputs);
    elbowIO.updateInputs(elbowInputs);
    Logger.processInputs("DoubleArm/Shoulder", shoulderInputs);
    Logger.processInputs("DoubleArm/Elbow", elbowInputs);
    Logger.recordOutput("DoubleArm/TargetShoulderRad", target.shoulderRad());
    Logger.recordOutput("DoubleArm/TargetElbowRad", target.elbowRad());
  }

  /** Move both joints to a named pose and hold there. */
  public Command goToPose(Pose pose) {
    return run(
        () -> {
          target = pose;
          shoulderIO.setAngle(pose.shoulderRad());
          elbowIO.setAngle(pose.elbowRad());
        });
  }
}
