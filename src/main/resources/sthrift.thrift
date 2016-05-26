#包名称，或者命名空间的定义
namespace java thrift

#数据结构的定义
struct BoolResult{
	1:optional bool result;	#返回结果
	2:optional i32  code;	#异常编码
	3:optional string msg;	#异常消息
}

#定义的方法
service ThriftService{
	BoolResult method(1:string userid,2:i64 from,3:i32 to);
	BoolResult methodPara(1:list<string> userids,2:i64 from,3:i32 to);
}
 