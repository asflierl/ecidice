package ecidice.model

class ActivityTracker {
  private var trackedActivities : List[Activity] = Nil
  
  def activities = trackedActivities
  
  def track(some: Activity) = (trackedActivities = some :: trackedActivities)
}
