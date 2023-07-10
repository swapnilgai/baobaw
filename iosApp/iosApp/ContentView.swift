import SwiftUI
import shared

struct ContentView: View {
   let greet = Greeting().greet()
    var authViewModel = AuthKoinViewModelModule().authViewModel
    @State var textFieldText: String = ""



    init() {
        observeState()
    }
   var body: some View {
        TextField("enter number", text: $textFieldText).padding()
            .background(Color.gray.opacity(0.3).cornerRadius(10))

        Button(action: {
            authViewModel.onSignUpClick(phoneNumber: textFieldText, password: "test")
        },
               label: {
            Text( "Send".uppercased())
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.blue.cornerRadius(10))
                .foregroundColor(.white)
                .font(.headline)

        })
   }

    private func observeState() {
        authViewModel.state.collect(
            collector: Collector<UiEvent<AuthState>> { state in onStateReceived(state: state) }
           ) { error in
               print("Error ocurred during state collection")
           }
       }


    public func onStateReceived(state: UiEvent<AuthState>) {
          switch UiEventKs(state){
          case .content(let obj):
              print("Phone ", obj.value?.content?.phone)
          case .error(let obj):
              print("Error ", obj)
          case .loading:
              print("Loading")
          default: print("Default")
          }
    }
}


struct ContentView_Previews: PreviewProvider {

   static var previews: some View {
      ContentView()
   }
}


func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {

    let aSet = NSCharacterSet(charactersIn:"0123456789").inverted
    let compSepByCharInSet = string.components(separatedBy: aSet)
    let numberFiltered = compSepByCharInSet.joined(separator: "")
    return string == numberFiltered
}
