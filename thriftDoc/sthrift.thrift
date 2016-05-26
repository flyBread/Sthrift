namespace java thrift

#布尔型返回值
struct BoolResult{
	1:optional bool result;	#返回结果
	2:optional i32  code;	#异常编码
	3:optional string msg;	#异常消息
}

service ThriftService{
	BoolResult serverState(1:string userid,2:i64 from,3:i32 to);
	BoolResult serverStatePara(1:list<string> userids,2:i64 from,3:i32 to);
}
 