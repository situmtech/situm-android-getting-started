## <a name="animateposition"></a>Animate position while walking
Sometimes the difference between a good work and a nice one relies on the little things. Here we want to show you how to animate the arrow position while walking, this feature will make the user experience much better with just a few changes, lets dive in!
First we are going to optimize the positioning not creating a point everytime we update the location but changing the position of the marker.

The second step is to animate the arrow everytime we walk. For this purpose we can use the ObjectAnimator class provided by android. We will need to create an UpdateListener in order to make the transition smoother everytime the position changes.

Once we have the animation for the position, we will see that it works much smoother than before, but we are missing something, the bearing. In order to make the arrow show us our direction, we will need to implement a new animation for this purpose. This is almost the same as the animation we did before. Everytime we update our position we get a message from our SDK with our exact position ¡and bearing!. Here we will have to deal with the angles in order to rotate always in the shortest way, after that just create another animation.

Aside from animate the position it is also interesting to animate the camera so the user is always seeing where he is. To do this you just need to animate the camera when the SDK returns a location.

<p align="center">
    <img src="/img/animate-position.gif" />
</p>

For a better and more fluid results, you have to set the `useDeadReckoning` option to true when starting the positioning.
This option allows you to get fast position updates using only the inertial sensors (compass, gyro...) without a decrease in the battery duration.

You can see how to animate the camera in the classes `AnimatePositionActivity` and `PositionAnimator`.
If you want to know more about location you can check the [SDK Documentation](http://developers.situm.es/sdk_documentation/android/javadoc/latest).
