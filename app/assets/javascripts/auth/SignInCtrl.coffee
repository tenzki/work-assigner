
class SignInCtrl

  constructor: (@$log, @$scope, @$alert, @$auth) ->
    @$scope.ctrl = @

  login: () ->
    @$auth.login({ identifier: @$scope.email, password: @$scope.password })
    .then(() =>
      @$alert
        content: 'You have successfully logged in'
        animation: 'fadeZoomFadeDown'
        type: 'material'
        duration: 3
    )
    .catch((response) =>
      @$alert
        content: response.data.message
        animation: 'fadeZoomFadeDown'
        type: 'material'
        duration: 3
    )

  authenticate: () ->
    @$auth.authenticate(provider)
    .then(() =>
      @$alert
        content: 'You have successfully logged in'
        animation: 'fadeZoomFadeDown'
        type: 'material'
        duration: 3
    )
    .catch((response) =>
      @$alert
        content: if response.data then response.data.message else response
        animation: 'fadeZoomFadeDown'
        type: 'material'
        duration: 3
    )

controllersModule.controller 'SignInCtrl', SignInCtrl
