
class SignUpCtrl

  constructor: (@$log, @$scope, @$alert, @$auth) ->
    @$scope.ctrl = @

  signup: () ->
    @$auth.signup(
      name: @$scope.displayName,
      identifier: @$scope.email,
      password: @$scope.password
    )
    .catch((response) =>
      if typeof response.data.message is 'object'
        angular.forEach(response.data.message, (message) =>
          @$alert
            content: message[0]
            animation: 'fadeZoomFadeDown'
            type: 'material'
            duration: 3
        )
      else
        @$alert
          content: response.data.message
          animation: 'fadeZoomFadeDown'
          type: 'material'
          duration: 3
    )

controllersModule.controller 'SignUpCtrl', SignUpCtrl
