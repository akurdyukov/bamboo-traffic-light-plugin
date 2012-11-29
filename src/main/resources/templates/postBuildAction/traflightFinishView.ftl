[#if plan?? && plan.buildDefinition?? && plan.buildDefinition.customConfiguration.get('custom.traflight.post.program.enabled')?? ]
    [@ui.bambooInfoDisplay titleKey='Post Build Traffic Light Program' float=false height='80px']

    [@ww.label label='Success Program' ]
        [@ww.param name='value']${plan.buildDefinition.customConfiguration.get('custom.traflight.post.program.success')!}[/@ww.param]
    [/@ww.label]

    [@ww.label label='Fail Program' ]
        [@ww.param name='value']${plan.buildDefinition.customConfiguration.get('custom.traflight.post.program.fail')!}[/@ww.param]
    [/@ww.label]

    [/@ui.bambooInfoDisplay]
[/#if]