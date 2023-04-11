---定义变量：redis中key值,redis中过期时间,规定的时间段内访问次数,当前访问次数
local key = KEYS[1]
local time = tonumber(ARGV[1])
local count = tonumber(ARGV[2])
local current = redis.call('get',key)
if current and tonumber(current) > count then ---如果current不为空当前访问次数大于规定时间段内的访问次数
    return tonumber(current)  ---返回当前访问的次数
end
    current = redis.call('incr',key)    --- 若没超过限制次数,当前次数+1（自增）
if tonumber(current)==1 then
    redis.call('expire',key,time)       --- 如果当前访问次数=1,表示第一次访问,设置当前key的过期时间
end
    return tonumber(current)