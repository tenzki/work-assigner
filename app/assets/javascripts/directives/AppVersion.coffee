
directivesModule.directive('appVersion', ['version', (version) ->
        (scope, elm) ->
            elm.text(version)
    ])
