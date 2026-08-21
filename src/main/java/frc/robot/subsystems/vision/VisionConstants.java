// ============================================================================
//  VisionConstants.java  -  settings for the Limelight cameras
// ----------------------------------------------------------------------------
//  STUDENTS: This describes our cameras to the code. The two most important
//  things are the CAMERA NAMES -- they must EXACTLY match the names you set in
//  each Limelight's web interface (http://<camera>.local:5801). If the name here
//  doesn't match, the code silently sees no data.
//
//  Our robot uses TWO cameras (this is the "second camera" example):
//    * camera0 = a Limelight 3 / Limelight 2 (a standalone smart camera)
//    * camera1 = the USB camera running on the SystemCore itself
//  Adding a third camera is as easy as adding camera2Name + a transform and one
//  more `new VisionIOLimelight(...)` line in RobotContainer.
// ============================================================================
package frc.robot.subsystems.vision;

import org.wpilib.apriltag.AprilTagFieldLayout;
import org.wpilib.apriltag.AprilTagFields;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.math.geometry.Transform3d;

public class VisionConstants {
  // The map of where every AprilTag sits on the field. kDefaultField auto-selects
  // the current season's field so vision pose estimates land in the right place.
  public static AprilTagFieldLayout aprilTagLayout =
      AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

  // ---- CAMERA NAMES (must match the names configured on each Limelight) ----
  // CAMERA 0: your Limelight 3 / Limelight 2. In the Limelight UI its default
  //   NetworkTables name is just "limelight"; rename it if you have several.
  public static String camera0Name = "limelight";
  // CAMERA 1: the USB camera driven by the SystemCore. Give the Limelight
  //   software a distinct name (e.g. "limelight-usb") so the two don't collide.
  public static String camera1Name = "limelight-usb";

  // ---- Where each camera sits on the robot (meters + radians) ----
  // NOTE: for Limelights these are actually configured in the camera's WEB UI,
  // not here, so the values below are only used for simulation/visualization.
  // (x = forward, y = left, z = up; rotation = roll, pitch, yaw)
  public static Transform3d robotToCamera0 =
      new Transform3d(0.2, 0.0, 0.2, new Rotation3d(0.0, -0.4, 0.0));
  public static Transform3d robotToCamera1 =
      new Transform3d(-0.2, 0.0, 0.2, new Rotation3d(0.0, -0.4, Math.PI));

  // ---- How picky we are about accepting a vision measurement ----
  public static double maxAmbiguity = 0.3; // reject single-tag guesses that are too uncertain
  public static double maxZError = 0.75; // reject poses that float above/below the floor

  // ---- How much we TRUST vision vs. the robot's own odometry ----
  // Smaller = trust vision more. These are "standard deviations" at 1 meter / 1 tag;
  // the code automatically trusts vision less when tags are far away or few.
  public static double linearStdDevBaseline = 0.02; // meters
  public static double angularStdDevBaseline = 0.06; // radians

  // Per-camera trust multiplier (index 0 = camera0, 1 = camera1). Raise a value
  // to trust that camera less (e.g. a lower-quality USB cam).
  public static double[] cameraStdDevFactors =
      new double[] {
        1.0, // Camera 0 (Limelight 3/2)
        1.0 // Camera 1 (USB cam on SystemCore) -- raise this to trust it less
      };

  // MegaTag2 (uses robot heading) is more stable for position but gives no
  // rotation info, so we trust its position more and ignore its rotation.
  public static double linearStdDevMegatag2Factor = 0.5;
  public static double angularStdDevMegatag2Factor = Double.POSITIVE_INFINITY;
}
