package com.loyer.common.redis.entity;

/**
 * redis info
 *
 * @author kuangq
 * @date 2021-12-03 14:18
 */
@SuppressWarnings({"unused", "AlibabaLowerCamelCaseVariableNaming"})
public class RedisInfo {

    /* TODO Server*/
    //redis服务版本
    private String redis_version;
    //Git SHA1
    private String redis_git_dirty;
    //Git dirty flag
    private String redis_git_sha1;
    //redis build id
    private String redis_build_id;
    //运行模式，单机/主从/集群
    private String redis_mode;
    //redis服务器的宿主操作系统
    private String os;
    //架构（32或64位）
    private String arch_bits;
    //使用的事件处理机制
    private String multiplexing_api;
    //编译redis时所使用的gcc版本
    private String gcc_version;
    //服务进程的pid
    private String process_id;
    //服务器的随机标识符
    private String run_id;
    //服务监听端口
    private String tcp_port;
    //启动总时间，单位是天
    private String uptime_in_days;
    //启动总时间，单位是秒
    private String uptime_in_seconds;
    //内部调度频率，程序规定serverCron每秒运行10次（进行关闭timeout的客户端，删除过期key等等）
    private String hz;
    //自增的时钟，用于LRU管理,该时钟100ms(hz=10,因此每1000ms/10=100ms执行一次定时任务)更新一次
    private String lru_clock;
    //可执行文件的路径
    private String executable;
    //配置文件路径
    private String config_file;
    //是否启用集群模式
    private String cluster_enabled;

    /*TODO Client*/
    //已连接客户端的数量（不包括通过slave连接的客户端）
    private String connected_clients;
    //当前连接的客户端当中，最长的输出列表，用client list命令观察omem字段最大值
    private String client_longest_output_list;
    //当前连接的客户端当中，最大输入缓存，用client list命令观察qbuf和qbuf-free两个字段最大值
    private String client_biggest_input_buf;
    //正在等待阻塞命令（BLPOP、BRPOP、BRPOPLPUSH）的客户端的数量
    private String blocked_clients;

    /*TODO Memory*/
    //使用内存，单位字节（byte）
    private String used_memory;
    //可读的used_memory
    private String used_memory_human;
    //系统给redis分配的内存即常驻内存，和top、ps等命令的输出一致
    private String used_memory_rss;
    //可读的used_memory_rss
    private String used_memory_rss_human;
    //内存使用的峰值大小
    private String used_memory_peak;
    //可读的used_memory_peak
    private String used_memory_peak_human;
    //操作系统的总内存 ，单位字节（byte）
    private String total_system_memory;
    //可读的total_system_memory
    private String total_system_memory_human;
    //lua引擎使用的内存
    private String used_memory_lua;
    //可读的used_memory_lua
    private String used_memory_lua_human;
    //最大分配内存
    private String maxmemory;
    //可读的maxmemory
    private String maxmemory_human;
    //达到最大内存的淘汰策略
    private String maxmemory_policy;
    //used_memory_rss和used_memory之间的比率，小于1表示使用了swap，大于1表示碎片比较多
    private String mem_fragmentation_ratio;
    //在编译时指定的redis所使用的内存分配器，可以是libc、jemalloc或者tcmalloc
    private String mem_allocator;

    /*TODO Persistence*/
    //服务器是否正在载入持久化文件
    private String loading;
    //离最近一次成功生成rdb文件，写入命令的个数，即有多少个写入命令没有持久化
    private String rdb_changes_since_last_save;
    //服务器是否正在创建rdb文件
    private String rdb_bgsave_in_progress;
    //最近一次成功创建rdb文件的时间戳
    private String rdb_last_save_time;
    //最近一次rdb持久化是否成功
    private String rdb_last_bgsave_status;
    //最近一次成功生成rdb文件使用的时间，单位S
    private String rdb_last_bgsave_time_sec;
    //如果服务器正在创建rdb文件，那么值记录的就是当前的创建操作已经耗费的时间，单位S
    private String rdb_current_bgsave_time_sec;
    //是否开启了aof，默认为0（没开启）
    private String aof_enabled;
    //标识aof的rewrite操作是否在进行中
    private String aof_rewrite_in_progress;
    //rewrite任务计划，当客户端发送bgrewriteaof指令，如果当前rewrite子进程正在执行，那么将客户端请求的bgrewriteaof变为计划任务，待aof子进程结束后执行rewrite
    private String aof_rewrite_scheduled;
    //最近一次aof rewrite耗费的时间，单位S
    private String aof_last_rewrite_time_sec;
    //如果rewrite操作正在进行，则记录所使用的时间
    private String aof_current_rewrite_time_sec;
    //上次bgrewriteaof操作的状态
    private String aof_last_bgrewrite_status;
    //上次aof写入状态
    private String aof_last_write_status;
    //aof当前大小，单位字节（byte）
    private String aof_current_size;
    //aof上次启动或rewrite的大小
    private String aof_base_size;
    //同上面的aof_rewrite_scheduled
    private String aof_pending_rewrite;
    //aof buffer的大小
    private String aof_buffer_length;
    //aof rewrite buffer的大小
    private String aof_rewrite_buffer_length;
    //后台IO队列中等待fsync任务的个数
    private String aof_pending_bio_fsync;
    //延迟的fsync计数器
    private String aof_delayed_fsync;

    /*TODO Stats*/
    //自启动起连接过的总数
    private String total_connections_received;
    //自启动起运行命令的总数
    private String total_commands_processed;
    //redis当前的qps，redis内部较实时的每秒执行的命令总数
    private String instantaneous_ops_per_sec;
    //网络入口流量字节数
    private String total_net_input_bytes;
    //网络出口流量字节数
    private String total_net_output_bytes;
    //网络入口kps
    private String instantaneous_input_kbps;
    //网络出口kps
    private String instantaneous_output_kbps;
    //拒绝的连接个数，redis连接个数达到maxclients限制，拒绝新连接的个数
    private String rejected_connections;
    //主从完全同步成功次数
    private String sync_full;
    //主从部分同步成功次数
    private String sync_partial_err;
    //主从部分同步失败次数
    private String sync_partial_ok;
    //自启动起过期的key的总数
    private String expired_keys;
    //自启动起剔除的key的数量（超过了maxmemory后）
    private String evicted_keys;
    //命中key的个数
    private String keyspace_hits;
    //未命中key的个数
    private String keyspace_misses;
    //当前使用中的频道数量
    private String pubsub_channels;
    //当前使用的模式的数量
    private String pubsub_patterns;
    //最近一次fork操作阻塞redis进程的时间（单位微妙μs）
    private String latest_fork_usec;
    //为迁移而打开的套接字数
    private String migrate_cached_sockets;

    /*TODO Replication*/
    //角色（主从）
    private String role;
    //从机数量
    private String connected_slaves;
    //从机信息
    private String slave0;
    //主从同步偏移量，此值如果和上面的offset相同说明主从一致没延迟
    private String master_repl_offset;
    //复制积压缓冲区是否开启
    private String repl_backlog_active;
    //复制积压缓冲大小（单位字节）
    private String repl_backlog_size;
    //复制缓冲区里偏移量的大小
    private String repl_backlog_first_byte_offset;
    //复制积压缓冲区中数据的大小（单位字节）
    private String repl_backlog_histlen;

    /*TODO CPU*/
    //将所有redis主进程在内核态所占用的CPU时求和累计起来
    private String used_cpu_sys;
    //将所有redis主进程在用户态所占用的CPU时求和累计起来
    private String used_cpu_user;
    //将后台进程在核心态所占用的CPU时求和累计起来
    private String used_cpu_sys_children;
    //将后台进程在用户态所占用的CPU时求和累计起来
    private String used_cpu_user_children;

    /*TODO Keyspace*/
    //db0的key的数量，带有生存期的key的数，平均存活时间
    private String db0;

    /*TODO Other*/
    private String active_defrag_hits;
    private String active_defrag_key_hits;
    private String active_defrag_key_misses;
    private String active_defrag_misses;
    private String active_defrag_running;
    private String allocator_active;
    private String allocator_allocated;
    private String allocator_frag_bytes;
    private String allocator_frag_ratio;
    private String allocator_resident;
    private String allocator_rss_bytes;
    private String allocator_rss_ratio;
    private String aof_last_cow_size;
    private String atomicvar_api;
    private String client_recent_max_input_buffer;
    private String client_recent_max_output_buffer;
    private String clients_in_timeout_table;
    private String configured_hz;
    private String expire_cycle_cpu_milliseconds;
    private String expired_stale_perc;
    private String expired_time_cap_reached_count;
    private String io_threaded_reads_processed;
    private String io_threaded_writes_processed;
    private String io_threads_active;
    private String lazyfree_pending_objects;
    private String master_replid;
    private String master_replid2;
    private String mem_aof_buffer;
    private String mem_clients_normal;
    private String mem_clients_slaves;
    private String mem_fragmentation_bytes;
    private String mem_not_counted_for_evict;
    private String mem_replication_backlog;
    private String module_fork_in_progress;
    private String module_fork_last_cow_size;
    private String number_of_cached_scripts;
    private String rdb_last_cow_size;
    private String rss_overhead_bytes;
    private String rss_overhead_ratio;
    private String second_repl_offset;
    private String slave_expires_tracked_keys;
    private String total_reads_processed;
    private String total_writes_processed;
    private String tracking_clients;
    private String tracking_total_items;
    private String tracking_total_keys;
    private String tracking_total_prefixes;
    private String unexpected_error_replies;
    private String used_memory_dataset;
    private String used_memory_dataset_perc;
    private String used_memory_overhead;
    private String used_memory_peak_perc;
    private String used_memory_scripts;
    private String used_memory_scripts_human;
    private String used_memory_startup;
}