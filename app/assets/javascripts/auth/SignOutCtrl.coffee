
class SignOutCtrl

  constructor: (@$alert, @$auth) ->
    if !@$auth.isAuthenticated()
      return

    @$auth.logout()
    .then(() =>
      @$alert
        content: 'You have been logged out'
        animation: 'fadeZoomFadeDown'
        type: 'material'
        duration: 3
  )

controllersModule.controller 'SignOutCtrl', SignOutCtrl