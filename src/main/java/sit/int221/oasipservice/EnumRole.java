package sit.int221.oasipservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public enum EnumRole {
    admin, lecturer, student;

//    private static final Map<String, ResourceType> NAME_MAP = Stream.of(values()).collect(Collectors.toMap(ResourceType::toString, Function.identity()));
//
//    public static ResourceType fromString(final String role){
//        ResourceType resourceType = NAME_MAP.get(role);
//        if(null == resourceType){
//            throw new IllegalArgumentException(String.format("fuck you",role, Arrays.asList(values())));
//        }
//        return resourceType;
//    }

//@ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public ErrorInfo handleException(HttpServletRequest request,
//                                     HttpServletResponse response, Exception ex) {
//        return new ErrorInfo(request, "stop being gay please");
//    }


}






























































