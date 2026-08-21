// ============================================================================
//  Main.java  -  the program's starting point
// ----------------------------------------------------------------------------
//  STUDENTS: This is like the "power button" for the robot code. When the
//  SystemCore boots your program, Java calls main() below, which hands control
//  to WPILib and tells it "the robot is described by the Robot class."
//
//  You should basically never change this file (only if you rename Robot).
// ============================================================================
package frc.robot;

import org.wpilib.wpilibj.RobotBase;

public final class Main {
  private Main() {}

  public static void main(String... args) {
    // Start the robot program, using our Robot class as the "brain".
    RobotBase.startRobot(Robot::new);
  }
}
