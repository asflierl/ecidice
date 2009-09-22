package ecidice.model

class ActivityTracker {
  private var trackedActivities : List[Timed] = Nil
  
  def activities = trackedActivities
  
  def track(some: Timed) = (trackedActivities = some :: trackedActivities)
}
