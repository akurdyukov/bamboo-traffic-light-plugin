[#if plan?? && plan.buildDefinition?? && plan.buildDefinition.customConfiguration.get('custom.traflight.pre.program.enabled')?? ]
        [@ui.bambooInfoDisplay titleKey='Pre Build Traffic Light Program' float=false height='80px']

        [@ww.label label='Program' ]
                [@ww.param name='value']${plan.buildDefinition.customConfiguration.get('custom.traflight.pre.program')!}[/@ww.param]
        [/@ww.label]

        [/@ui.bambooInfoDisplay]
[/#if]