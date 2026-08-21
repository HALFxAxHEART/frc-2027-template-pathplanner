// ============================================================================
//  Constants.java  -  chooses whether we are on a REAL robot, SIMULATION,
//                     or REPLAYING a past log
// ----------------------------------------------------------------------------
//  STUDENTS: AdvantageKit (our logging library) can run the SAME robot code in
//  three different "modes". Understanding these three modes is the key idea of
//  AdvantageKit:
//
//    REAL   -> running on the actual robot / SystemCore. Reads real sensors,
//              drives real motors, and SAVES a log of everything.
//    SIM    -> running on your laptop with a physics simulation. No hardware
//              needed! Great for writing and testing code at home.
//    REPLAY -> takes a log file from a real match and re-runs your code against
//              it, as if the robot were live again. This lets you add new logged
//              values AFTER the fact and see exactly what the robot "saw".
//
//  The mode is picked automatically: if we're on real hardware -> REAL,
//  otherwise we use whatever "simMode" is set to below.
// ============================================================================
package frc.robot;

import org.wpilib.wpilibj.RobotBase;

public final class Constants {
  /**
   * When NOT on a real robot, should we run a physics SIM or REPLAY a log? Change this to
   * Mode.REPLAY only when you are doing log replay.
   */
  public static final Mode simMode = Mode.SIM;

  /** The mode we are actually running in right now (decided automatically). */
  public static final Mode currentMode = RobotBase.isReal() ? Mode.REAL : simMode;

  public static enum Mode {
    /** Running on a real robot. */
    REAL,

    /** Running a physics simulator. */
    SIM,

    /** Replaying from a log file. */
    REPLAY
  }
}
