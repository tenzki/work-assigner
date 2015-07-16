class NavBarCtrl

  constructor: (@$scope, @$auth) ->
    @$scope.nav = @

  isAuthenticated: () ->
    @$auth.isAuthenticated()

controllersModule.controller 'NavBarCtrl', NavBarCtrl