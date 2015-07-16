
dependencies = [
  'ui.bootstrap'
  'satellizer'
  'ngMessages'
  'ui.router'
  'mgcrea.ngStrap'
  'work-assigner.filters'
  'work-assigner.services'
  'work-assigner.controllers'
  'work-assigner.directives'
  'work-assigner.common'
]

app = angular.module 'work-assigner', dependencies

app.run ($rootScope) ->
  $rootScope.user = {}

app
.config ($locationProvider, $stateProvider, $urlRouterProvider) ->

  authenticate = ($q, $location, $auth) ->
    deferred = $q.defer();
    if !$auth.isAuthenticated()
      $location.path('/login')
    else
      deferred.resolve()
    deferred.promise

  handleLoggedIn = ($q, $location, $auth) ->
    deferred = $q.defer();
    if $auth.isAuthenticated()
      $location.path('/')
    else
      deferred.resolve()
    deferred.promise

  $locationProvider.html5Mode
    enabled: false,
    requireBase: false

  $stateProvider
  .state 'homepage',
    url: '/'
    controller: 'TaskCtrl'
    templateUrl: '/assets/partials/view.html'
    resolve:
      authenticate: authenticate
#  .state 'timezone-create',
#    url: '/timezone/create'
#    controller: 'CreateTimezoneCtrl'
#    templateUrl: '/assets/partials/create.html'
#    resolve:
#      authenticate: authenticate
#  .state 'timezone-edit',
#    url: '/timezone/edit/:id'
#    controller: 'UpdateTimezoneCtrl'
#    templateUrl: '/assets/partials/update.html'
#    resolve:
#      authenticate: authenticate
  .state 'login',
    url: '/login'
    controller: 'SignInCtrl'
    templateUrl: '/assets/partials/login.html'
    resolve:
      authenticate: handleLoggedIn
  .state 'sign-up',
    url: '/signUp'
    controller: 'SignUpCtrl'
    templateUrl: '/assets/partials/signUp.html'
    resolve:
      authenticate: handleLoggedIn
  .state 'logout',
    url: '/signOut'
    controller: 'SignOutCtrl'
    templateUrl: null

  $urlRouterProvider.otherwise '/'

.config ($httpProvider) ->
  $httpProvider.interceptors.push ($q, $injector) ->
    request: (request) ->
      $auth = $injector.get('$auth')
      request.headers['X-Auth-Token'] = $auth.getToken() if $auth.isAuthenticated()
      request
    responseError: (rejection) ->
      $injector.get('$auth').logout() if rejection.status is 401
      $q.reject(rejection)
.config ($authProvider) ->
  $authProvider.httpInterceptor = true; # Add Authorization header to HTTP request
  $authProvider.loginOnSignup = true;
  $authProvider.loginRedirect = '/';
  $authProvider.logoutRedirect = '/login';
  $authProvider.signupRedirect = '/login';
  $authProvider.loginUrl = '/auth/signIn/credentials';
  $authProvider.signupUrl = '/auth/signUp';
  $authProvider.loginRoute = '/login';
  $authProvider.signupRoute = '/signUp';
  $authProvider.tokenName = 'token';
  $authProvider.tokenPrefix = 'satellizer'; # Local Storage name prefix
  $authProvider.authHeader = 'X-Auth-Token';

@commonModule = angular.module('work-assigner.common', [])
@controllersModule = angular.module('work-assigner.controllers', [])
@servicesModule = angular.module('work-assigner.services', [])
@modelsModule = angular.module('work-assigner.models', [])
@directivesModule = angular.module('work-assigner.directives', [])
@filtersModule = angular.module('work-assigner.filters', [])