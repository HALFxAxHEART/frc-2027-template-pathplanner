// ============================================================================
//  Robot.java  -  the robot's "main loop" AND where AdvantageKit logging starts
// ----------------------------------------------------------------------------
//  STUDENTS: This is the most important file for understanding "log everything".
//
//  We extend LoggedRobot (from AdvantageKit) instead of the normal TimedRobot.
//  LoggedRobot does everything TimedRobot does -- it calls teleopPeriodic() etc.
//  about 50 times per second -- but it ALSO records a complete log of every
//  input the robot saw and every value we log, so we can replay it later.
//
//  The Logger setup below is the heart of the logging system:
//    1. We record "metadata" (which exact code build produced this log).
//    2. Depending on the mode (REAL / SIM / REPLAY) we choose WHERE the log goes:
//         - REAL:  write a .wpilog file on a USB stick (/U/logs) AND stream live
//                  to NetworkTables so AdvantageScope can watch in real time.
//         - SIM:   just stream live to NetworkTables.
//         - REPLAY: read an old log back in and re-run the code against it.
//    3. Logger.start() turns it all on.
//
//  After that, robotPeriodic() runs the Command Scheduler, which is what makes
//  all our subsystems and button bindings actually do things.
// ============================================================================
package frc.robot;

import org.littletonrobotics.junction.LogFileUtil;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGReader;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;
import org.wpilib.wpilibj2.command.Command;
import org.wpilib.wpilibj2.command.CommandScheduler;

public class Robot extends LoggedRobot {
  // The command that runs during autonomous (chosen on the dashboard).
  private Command autonomousCommand;

  // Holds all our subsystems and button bindings (see RobotContainer.java).
  private RobotContainer robotContainer;

  public Robot() {
    // ---- 1. Record metadata: stamp the log with exactly which code built it ----
    // This means every log file "knows" the git commit and build date it came
    // from -- super useful when debugging "which version of the code did this?".
    Logger.recordMetadata("ProjectName", BuildConstants.MAVEN_NAME);
    Logger.recordMetadata("BuildDate", BuildConstants.BUILD_DATE);
    Logger.recordMetadata("GitSHA", BuildConstants.GIT_SHA);
    Logger.recordMetadata("GitDate", BuildConstants.GIT_DATE);
    Logger.recordMetadata("GitBranch", BuildConstants.GIT_BRANCH);
    Logger.recordMetadata(
        "GitDirty",
        switch (BuildConstants.DIRTY) {
          case 0 -> "All changes committed";
          case 1 -> "Uncommitted changes";
          default -> "Unknown";
        });

    // ---- 2. Decide WHERE the log data goes, based on the current mode ----
    switch (Constants.currentMode) {
      case REAL:
        // On the real robot: save a log file to a USB stick, AND publish live to
        // NetworkTables so AdvantageScope can watch while we drive.
        // NOTE: on our SystemCore Pi, plug in a USB stick for the .wpilog file. If
        // you have no USB stick, you can change this to a folder path instead, e.g.
        //   new WPILOGWriter("/home/systemcore/logs")
        Logger.addDataReceiver(new WPILOGWriter());
        Logger.addDataReceiver(new NT4Publisher());
        break;

      case SIM:
        // In simulation on your laptop: just publish live to NetworkTables.
        Logger.addDataReceiver(new NT4Publisher());
        break;

      case REPLAY:
        // Replay: read an old log and re-run the code against it as fast as possible.
        setUseTiming(false);
        String logPath = LogFileUtil.findReplayLog();
        Logger.setReplaySource(new WPILOGReader(logPath));
        Logger.addDataReceiver(new WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_sim")));
        break;
    }

    // ---- 3. Turn logging on. From here, "log everything" is active. ----
    Logger.start();

    // Build all subsystems + button bindings.
    robotContainer = new RobotContainer();
  }

  /** Runs about 50x/second in EVERY mode. This is where the command scheduler ticks. */
  @Override
  public void robotPeriodic() {
    // The Command Scheduler polls the joystick buttons, runs active commands, and
    // calls every subsystem's periodic() method. Without this line, nothing moves.
    CommandScheduler.getInstance().run();
  }

  /** Called once when the robot becomes disabled. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** Called once when autonomous starts -- schedules the auto command we picked. */
  @Override
  public void autonomousInit() {
    autonomousCommand = robotContainer.getAutonomousCommand();
    if (autonomousCommand != null) {
      CommandScheduler.getInstance().schedule(autonomousCommand);
    }
  }

  @Override
  public void autonomousPeriodic() {}

  /** Called once when teleop (driver control) starts. */
  @Override
  public void teleopInit() {
    // Stop the autonomous command so it doesn't fight the driver.
    if (autonomousCommand != null) {
      autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  /** "Utility" mode replaces the old "test" mode in WPILib 2027. */
  @Override
  public void utilityInit() {
    // Cancel everything when entering utility mode.
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void utilityPeriodic() {}
}
